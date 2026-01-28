# DTO架构重构文档

## 概述

根据MVP V2.0架构设计，我们将DTO重新组织为三类，分别对应三种边界：

1. **模块间交互DTO** - 处理模块间的通信契约
2. **对外API DTO** - 处理系统与外部客户端的接口
3. **数据流转DTO** - 处理模块内部的数据传输

## 新包结构

### 1. 模块间交互DTO
**包路径**: `com.archimedes.backend.embedding.api.model`

| DTO名称 | 用途 | 特点 |
|---------|------|------|
| `VectorizeBatchRequest` | Ingestion → Embedding | 批量优先，支持1000条文本 |
| `VectorizeBatchResponse` | Embedding → Ingestion | Fail-safe设计，部分失败不影响整体 |
| `VectorizeSingleRequest` | Search → Embedding | 实时查询，低延迟优化 |

### 2. 对外API DTO
**包路径**: `com.archimedes.backend.search.model.dto`

| DTO名称 | 用途 | 特点 |
|---------|------|------|
| `SearchRequest` | 前端 → Search | 封装查询、过滤、分页参数 |
| `PaperVO` | Search → 前端 | 脱敏处理，摘要截断到300字符 |
| `SearchResponse` | Search → 前端 | 标准化信封，包含分页和性能指标 |

### 3. 数据流转DTO
**包路径**: `com.archimedes.backend.ingestion.model`

| DTO名称 | 用途 | 特点 |
|---------|------|------|
| `RawPaperDTO` | Source → Pipeline | 容错设计，所有字段允许null |
| `ProcessedPaper` | Pipeline → Sink | 富集模型，包含向量和质量评分 |
| `IngestionSummary` | Pipeline → Admin | 可观测性，详细的执行统计 |

## 数据流向

```text
[ 数据源 ]
    ↓ (JSON解析)
[ RawPaperDTO ]          <-- 脏数据进入管道
    ↓ (清洗转换)
    ↓ → [ VectorizeBatchRequest ] → [ Embedding模块 ]
    ↓ ← [ VectorizeBatchResponse ] ←
    ↓
[ ProcessedPaper ]       <-- 清洗且带向量的数据
    ↓ (持久化)
[ 数据库/ES ]

---

[ 前端 ]
    ↓ → [ SearchRequest ] → [ Search模块 ]
    ↓                           ↓
    ↓                           ↓ → [ VectorizeSingleRequest ] → [ Embedding模块 ]
    ↓                           ↓ ← [ float[] vector ]        ←
    ↓                           ↓ → (查询ES/DB)
    ↓                           ↓ ← [ 原始数据 ]
    ↓                           ↓ → (转换为PaperVO)
    ↓ ← [ SearchResponse ]   ←  ↓
```

## 设计原则

### 1. 边界清晰
- 每个DTO只服务于特定的边界和用途
- 避免跨边界共享DTO，防止耦合

### 2. 失败安全
- 批量操作支持部分失败，不影响整体流程
- 使用Optional和Builder模式提高容错性

### 3. 性能优化
- 批量操作优先，减少网络往返
- 对外API包含分页和截断逻辑
- 支持缓存的单条查询

### 4. 可观测性
- 包含执行时间、成功率等指标
- 提供详细的错误分类和统计

## 迁移说明

### 已更新的文件
- `SearchController` - 使用新的SearchRequest/Response
- `SearchService` - 接口和实现都已更新
- `DTOConverter` - 方法签名更新，支持新的DTO转换
- `ArxivIngestionPipeline` - 使用新的Ingestion DTO
- `IngestionSource` - 使用新的RawPaperDTO

### 待适配的组件
1. **Embedding模块实现** - 需要实现EmbeddingService接口
2. **数据库映射** - Entity类可能需要调整字段映射
3. **配置文件** - 可能需要更新包扫描路径

## 向后兼容

为了平滑迁移，建议：
1. 保留旧的DTO类一段时间（标记为@Deprecated）
2. 提供适配器类处理新旧DTO转换
3. 逐步迁移各个模块，避免一次性大改

## 测试策略

1. **单元测试** - 验证每个DTO的序列化/反序列化
2. **集成测试** - 验证模块间的数据传输
3. **性能测试** - 验证批量处理的吞吐量
4. **容错测试** - 验证部分失败场景的处理