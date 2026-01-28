# Archimedes MVP 架构设计

## 1. 模块设计

### 1.0 架构概览

**当前项目采用经典分层架构，包结构如下：**
```text
com.example.archimedes/
├── dto/                      # 统一数据传输对象
│   ├── request/              # 请求DTO (SearchRequest, VectorizeRequest)
│   ├── response/             # 响应DTO (SearchResponse, PaperVO, VectorizeResponse)  
│   └── internal/             # 内部流转DTO (RawPaper, ProcessedPaper, IngestionSummary)
├── service/                  # 业务服务接口层
│   ├── search/SearchService.java
│   ├── embedding/EmbeddingService.java
│   └── ingestion/IngestionService.java
├── controller/               # REST API控制器
├── exception/                # 统一异常体系
│   ├── BusinessException     # 业务异常基类
│   ├── SearchException       # 搜索异常
│   ├── EmbeddingException    # 向量化异常
│   └── IngestionException    # 摄取异常
├── config/                   # Spring配置类
├── entity/                   # 数据库实体映射 (Paper, Author, etc.)
├── mapper/                   # 数据访问层 (MyBatis)
├── utils/                    # 工具类 (DTOConverter, etc.)
├── search/                   # 搜索模块实现
├── embedding/                # 向量化模块实现
└── ingestion/                # 数据摄取模块实现
```

1.1 Ingestion Module
Ingestion Pipeline 是负责数据从原始存储（文件/S3）流向系统数据库（MySQL & ES）的批处理模块。
- 单一职责: 读取原始 JSONL 数据，清洗、向量化，并持久化到存储层。
- 运行模式: 离线批处理 (Offline Batch Job)。
- 资源特征: I/O 密集型（读文件/写库）与 CPU 密集型（向量化）混合。
- 核心约束:
  - Streaming First: 严禁一次性将整个数据集读入内存。必须采用流式处理。
  - Fault Tolerance: 单条数据的脏数据或异常不应导致整个任务崩溃（Fail-safe）。
TriggerIngestion
采用模板方法 
- Usage: 由 Admin API 或启动脚本调用。
- Input:
  - sourcePath (String/URI): 数据源路径（本地文件路径或 URL）。
    - Constraint: File extension must be .json or .jsonl.
  - jobId (String): 用于追踪本次任务的唯一标识（UUID）。
- Output:
  - JobSummary (Object): 包含 successCount, failureCount, duration。
- Behavior:
  - 该调用应默认为同步阻塞（MVP阶段简单化），或者返回一个 Future。
Extract (输入)
采用 策略模式 处理数据源的多样性
- Mechanism: 使用基于游标（Cursor）或迭代器（Iterator）的 JSON 解析器（如 Jackson JsonParser）。
- Constraint: 内存中同一时刻存在的原始 JSON 对象数量不得超过 batch.size * 2。
- Mapping: 将原始 JSON 映射为内部 DTO (RawPaperDTO)。
Transform (处理)
对读取到的每一条记录执行以下操作（串行或并行均可，但必须线程安全）：
1. Sanitization (清洗):
  - 去除 Title/Abstract 中的 LaTeX 换行符（\n）和多余空格。
  - 标准化日期格式（统一转为 yyyy-MM-dd）。
2. Validation (校验):
  - 检查必要字段 (id, title, abstract) 是否缺失。如果缺失，标记为 Skipped 并记录日志。
3. Vectorization (向量化 - 关键步骤):
  - Batching Strategy: 必须积攒满一个 batch.size（例如 32 条）后，调用 EmbeddingEngine.EmbedBatch 接口。
  - Reason: 避免单条调用导致的上下文切换开销，最大化 CPU 利用率。
  - Merging: 将返回的向量回填到 DTO 中。
Load （写入）
处理完一个 Batch 的数据后，执行持久化。这里涉及双写一致性策略。
- Action A: Write to MySQL (Source of Truth)
  - 使用 JDBC Batch Insert (INSERT INTO papers ... values (...), (...)).
  - Idempotency Strategy: 使用 INSERT IGNORE 或 ON DUPLICATE KEY UPDATE。如果 ID 已存在，覆盖元数据。
- Action B: Write to Elasticsearch (Search Index)
  - 使用 ES Bulk API。
  - Document ID: 必须使用 Arxiv ID 作为 ES 的 _id，确保多次写入覆盖同一个文档，不产生副本。
- Transaction Boundary (事务边界):
  - MVP 阶段不要求分布式事务（XA）。
  - 顺序: 先写 MySQL，成功后再写 ES。
  - Failure Handling: 如果 MySQL 成功但 ES 失败，记录 ERROR 日志（包含 Paper IDs），但不回滚 MySQL。这属于 "At-least-once" 策略，优先保证数据入库。

 // 策略模式
 public interface IngestionSource extends AutoCloseable {
     /**
      * 返回数据的迭代器。
      * 既可以是读取文件的一行，也可以是 RSS 的一个 Item，或者 API 的一页。
      */
     Iterator<RawPaper> openStream();

     /**
      * 数据源的描述，用于日志 (e.g., "File: /data/arxiv.json" or "RSS: http://...")
      */
     String getSourceDescription();
 }
 
 // 模板方法
 /**
 * Ingestion 模块的核心接口 
 */
public interface IngestionPipeline {
    /**
     * 触发摄入任务的入口 [cite: 490]
     * @param source 摄入的数据源策略 [cite: 582]
     * @return 包含成功/失败计数的总结报告 [cite: 498, 592]
     */
    IngestionSummary triggerIngestion(IngestionSource source);
}

/**
 * 这是一个具体的摄入引擎，不再是抽象类。
 * 它通过组合 IngestionSource (策略) 和 EmbeddingEngine 来完成工作。
 */
public class ArxivIngestionPipeline implements IngestionPipeline {

    private final EmbeddingEngine embeddingEngine; // 注入向量化引擎
    
    public ArxivIngestionPipeline(EmbeddingEngine embeddingEngine) {
        this.embeddingEngine = embeddingEngine;
    }

    @Override
    public final IngestionSummary triggerIngestion(IngestionSource source) {
        try (source) {
            Iterator<RawPaper> iterator = source.openStream(); // EXTRACT
            
            while (iterator.hasNext()) {
                List<RawPaper> chunk = readNextChunk(iterator);
                
                // TRANSFORM (直接调用内部实现，不再需要子类重写)
                List<ProcessedPaper> processed = transform(chunk); 
                
                // LOAD (直接执行双写逻辑)
                load(processed); 
            }
        } catch (Exception e) {
            handleError(e);
        }
        return getSummary();
    }

    // --- 内部标准逻辑：只需实现一次 ---

    private List<ProcessedPaper> transform(List<RawPaper> chunk) {
        // 1. 调用 Sanitizer 清洗 LaTeX 换行符等
        // 2. 调用 embeddingEngine 获取向量
        return null; 
    }

    private void load(List<ProcessedPaper> batch) {
        // 1. 写入 MySQL (Source of Truth)
        // 2. 同步写入 Elasticsearch
    }
}

class IngestionSummary {
     long totalProcessed;
     long successCount;
     long skippedCount;
     Duration duration;
 }

## 2. 当前架构实现状态

### 2.1 统一DTO设计

#### 请求层DTO (`dto.request`)

**SearchRequest** - 搜索请求统一入口
```java
public class SearchRequest {
    @Size(max = 200, message = "查询文本长度不能超过200字符")
    private String query;                    // 搜索查询词
    private List<String> categories;         // 分类筛选
    private DateRange dateRange;             // 日期范围筛选
    @Min(value = 1) @Max(value = 1000)
    private int page = 1;                    // 页码（从1开始）
    @Min(value = 1) @Max(value = 100)  
    private int size = 20;                   // 每页大小
    private boolean enableSemanticSearch = true; // 是否启用语义搜索
    
    // 业务方法
    public boolean isPureFilterMode();       // 判断是否为纯筛选模式
    public String getEffectiveQuery();       // 获取有效查询文本
    public int getOffset();                  // 计算分页偏移量
}
```

**VectorizeRequest** - 向量化请求统一入口
```java 
public class VectorizeRequest {
    @NotNull @NotEmpty
    @Size(max = 500, message = "单次请求不能超过500条文本")
    private List<String> texts;             // 待向量化文本列表
    
    // 静态工厂方法
    public static VectorizeRequest single(String text);
    public static VectorizeRequest batch(List<String> texts);
    public boolean isSingle();              // 是否为单条请求
    public int getCount();                  // 获取文本数量
}
```

#### 响应层DTO (`dto.response`)

**SearchResponse** - 搜索结果标准响应
```java
public class SearchResponse {
    private List<PaperVO> items;            // 搜索结果列表（永不为null）
    private long totalHits;                 // 总命中数
    private int currentPage;                // 当前页码
    private int pageSize;                   // 每页大小
    private int totalPages;                 // 总页数
    private long tookMillis;                // 查询耗时
    private boolean usedSemanticSearch;     // 是否使用语义搜索
    
    // 便捷方法
    public boolean hasNextPage();
    public boolean hasPreviousPage();
    public boolean isEmpty();
    
    // 静态工厂方法
    public static SearchResponse empty(int page, int size, long tookMillis);
    public static SearchResponse success(List<PaperVO> items, ...);
}
```

**PaperVO** - 论文视图对象（脱敏后）
```java
public class PaperVO {
    private String id;                      // ArXiv ID
    private String title;                   // 论文标题
    private String abstractText;            // 摘要（已截断）
    private List<String> authors;           // 作者列表
    private LocalDate publishDate;          // 发布日期
    private List<String> categories;        // 分类列表
    private String url;                     // ArXiv URL
    private Double relevanceScore;          // 相关性评分（语义搜索时）
    private boolean abstractTruncated;      // 摘要是否被截断
    private List<String> highlights;        // 高亮片段
}
```

**VectorizeResponse** - 向量化结果响应
```java
public class VectorizeResponse {
    private List<float[]> vectors;          // 向量结果列表
    private List<Integer> failedIndices;    // 失败的索引列表
    private long processingTimeMs;          // 处理耗时
    
    // 便捷方法
    public boolean isAllSuccess();
    public int getSuccessCount();
    public float[] getSingleVector();       // 单条请求使用
    
    // 静态工厂方法  
    public static VectorizeResponse success(List<float[]> vectors, long time);
    public static VectorizeResponse withFailures(List<float[]> vectors, 
                                                  List<Integer> failedIndices, long time);
}
```

#### 内部流转DTO (`dto.internal`)

**RawPaper** - 原始论文数据载体
```java
public class RawPaper {
    private String id;                      // ArXiv ID（原始）
    private String title;                   // 原始标题
    private String abstractText;            // 原始摘要
    private String publishDate;             // 原始日期字符串
    private String[] categories;            // 原始分类数组
    private String[] authors;               // 原始作者数组
    private String url;                     // ArXiv URL
    private String sourceType;              // 数据来源标识
    
    // 校验方法
    public boolean isValid();               // 检查数据完整性
    public String getCleanId();             // 获取清理后ID
}
```

**ProcessedPaper** - 处理后论文数据
```java
public class ProcessedPaper {
    private String id;                      // 清理后ID
    private String title;                   // 清洗后标题
    private String abstractText;            // 清洗后摘要
    private LocalDate publishDate;          // 解析后日期
    private List<String> authors;           // 清洗后作者列表
    private List<String> categories;        // 清洗后分类列表
    private String url;                     // 标准化URL
    private float[] vector;                 // 生成的向量
    private LocalDateTime processedAt;      // 处理时间戳
    private int qualityScore;               // 质量评分（0-100）
    
    // 业务方法
    public boolean isComplete();            // 检查数据完整性
    public String getCombinedText();        // 获取向量化文本
    public String getPrimaryCategory();     // 获取主要分类
}
```

**IngestionSummary** - 摄取任务总结
```java
public class IngestionSummary {
    private String jobId;                   // 任务ID
    private String sourceDescription;       // 数据源描述
    private LocalDateTime startTime;        // 开始时间
    private LocalDateTime endTime;          // 结束时间
    private long totalProcessed;            // 总处理数量
    private long successCount;              // 成功数量
    private long failureCount;              // 失败数量
    private long skippedCount;              // 跳过数量
    private Map<String, Integer> errorCategories;  // 错误分类统计
    private List<String> warnings;          // 警告信息
    
    // 计算方法
    public Duration getTotalDuration();
    public double getSuccessRate();
    public boolean isSuccessful();          // 成功率 >= 95%
    public boolean needsAttention();        // 失败率 > 5% 或有警告
}
```

### 2.2 服务接口定义

#### EmbeddingService - 向量化服务
```java
public interface EmbeddingService {
    /**
     * 向量化文本（统一入口，支持单条和批量）
     */
    VectorizeResponse vectorize(VectorizeRequest request);
    
    /**
     * 获取向量维度
     */
    int getVectorDimension();
    
    /**
     * 检查服务可用性
     */
    boolean isAvailable();
    
    /**
     * 预热服务
     */
    void warmUp();
}
```

#### SearchService - 搜索服务  
```java
public interface SearchService {
    /**
     * 执行搜索（统一入口）
     */
    SearchResponse search(SearchRequest request);
}
```

#### IngestionService - 摄取服务
```java
public interface IngestionService {
    /**
     * 处理原始论文数据
     */
    IngestionSummary process(List<RawPaper> rawPapers);
    
    /**
     * 从文件摄取数据
     */
    IngestionSummary ingestFromFile(String filePath);
}
```

### 2.3 统一异常体系

**异常层次结构**
```java
BusinessException                       // 业务异常基类
├── SearchException                     // 搜索异常
├── EmbeddingException                  // 向量化异常  
└── IngestionException                  // 摄取异常
```

### 2.4 数据流向图

```text
┌─────────────┐    RawPaper        ┌─────────────┐    VectorizeRequest
│ Data Source │ ─────────────────→ │ Ingestion   │ ──────────────────→
└─────────────┘                    │ Pipeline    │                   
                                   └─────────────┘    VectorizeResponse
                                           │      ←──────────────────
                                           ▼ ProcessedPaper          
┌─────────────┐    SearchRequest   ┌─────────────┐    VectorizeRequest
│  Frontend   │ ─────────────────→ │   Search    │ ──────────────────→
└─────────────┘                    │   Module    │                   
       ▲                           └─────────────┘    float[] vector  
       │ SearchResponse                     │      ←──────────────────
       └────────────────────────────────────┘                       
                    PaperVO                                           
```

### 2.5 实体模型设计

**数据库实体 (`entity`)**
```java
@Entity
public class Paper {
    private String id;                      // ArXiv ID (主键)
    private String title;                   // 论文标题
    private String abstractText;            // 摘要全文
    private LocalDate publishedDate;        // 发布日期
    private String categories;              // 分类（逗号分隔）
    private String url;                     // ArXiv URL
    private float[] embedding;              // 向量数据
}

@Entity 
public class Author {
    private Long id;                        // 自增主键
    private String name;                    // 作者姓名
}

@Entity
public class PaperAuthor {
    private String paperId;                 // 论文ID
    private Long authorId;                  // 作者ID
    private int authorOrder;                // 作者顺序
}
```

## 3. 核心设计原则

### 3.1 分层职责
- **Controller层**: 仅负责参数校验和HTTP响应，不包含业务逻辑
- **Service层**: 核心业务逻辑，模块间协调
- **DTO层**: 类型安全的数据传输，明确边界
- **Entity层**: 数据持久化映射

### 3.2 异常处理策略
- **统一异常基类**: `BusinessException` 提供错误码和消息
- **模块专用异常**: 继承基类，提供特定错误上下文
- **失败安全**: 批量操作部分失败不影响整体流程

### 3.3 性能优化
- **批量优先**: 向量化采用批量处理提高吞吐量
- **流式处理**: Ingestion 模块避免大数据集内存溢出
- **响应截断**: 前端API返回数据经过适当截断处理
1.2 Embedding Module
是一个无状态（Stateless）、确定性（Deterministic）的转换器。

**当前实现接口：**
```java
public interface EmbeddingService {
    VectorizeResponse vectorize(VectorizeRequest request);
    int getVectorDimension();
    boolean isAvailable(); 
    void warmUp();
}
```

EmbedSingle (单文本向量化)
- Usage: 主要被 Search Module 使用，用于将用户的查询词（Query）转为向量。
- Input:
  - VectorizeRequest.single(text): 单条文本的向量化请求
- Output:
  - VectorizeResponse: 包含单个向量的响应对象
- Error Behavior:
  - 如果输入为空或纯空白字符，抛出 EmbeddingException

EmbedBatch (批量向量化)
- Usage: 主要被 Ingestion Module 使用，用于在数据导入时提高吞吐量（Throughput）。
- Input:
  - VectorizeRequest.batch(texts): 批量文本的向量化请求
- Output:
  - VectorizeResponse: 包含向量列表和失败索引的响应对象
- Constraint:
  - 输出列表的大小必须等于输入列表的大小。失败的位置在vectors中为null，同时记录在failedIndices中
1.3 Search Module
传统的 MVC 架构, 提供和用户对接的 Controller, 以及业务处理的 Service
Search Module 是 Archimedes 系统的核心业务编排单元。它是一个纯逻辑层（Logic Layer），负责协调“语义理解”与“底层检索”两个子系统。
- 单一职责: 接收并校验查询请求，编排 Embedding Engine 与 Elasticsearch 的交互，并将结果转换为领域模型。
- 黑盒交付: 上层（Controller/API Layer）无需关心本次搜索是走了向量检索、关键词检索还是混合检索，只需关心输入查询对象和输出结果列表。
- 核心约束:
  - Read-Only: 本模块严禁执行任何修改底层数据（Write/Update/Delete）的操作。
  - Stateless: 模块内部不得持有任何请求级别的状态，必须支持水平扩展。
ExecuteSearch (执行检索)
- Usage: 被 API 层调用，响应用户的实时搜索请求。
- Input:
  - Query Text (String): 原始搜索词。
    - Constraint: 允许为空（此时退化为纯筛选模式）。
    - Constraint: 若不为空，长度必须限制在配置定义的 max_query_length 以内（e.g., 200字符）。
  - Filters (Structure): 结构化的筛选条件（年份范围、分类枚举）。
    - Constraint: 年份范围必须符合逻辑（Start <= End）。
  - Pagination (Structure): 分页参数。
    - Constraint: Page Size 必须强制限制在配置定义的 max_page_size 以内，严禁透传前端的大数值。
- Output:
  - Result List (List): 命中的文档列表。
    - Guarantee: 永远不返回 Null。无结果时返回空列表。
    - Sanitization: 返回的 Abstract (摘要) 字段必须经过截断或高亮处理，严禁直接返回数据库中存储的原始长文本（以防响应体过大）。
  - Total Hits (Long): 近似命中总数。
- Error Behavior:
  - Validation Error: 输入参数违规（如页码 < 1），抛出 InvalidSearchRequestException。
  - Infrastructure Error: 若 Elasticsearch 连接彻底失败，抛出 SearchInfrastructureException。
  - Degradation: 若 Embedding 生成失败，不抛出异常，而是静默降级为关键词搜索（详见 Internal Processing Logic）。