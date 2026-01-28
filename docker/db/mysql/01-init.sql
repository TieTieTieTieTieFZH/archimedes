-- 创建数据库（如果不存在），强制使用 utf8mb4 字符集
CREATE DATABASE IF NOT EXISTS archimedes_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE archimedes_db;

-- ==========================================
-- 1. 核心表: papers (Source of Truth)
-- ==========================================
CREATE TABLE papers (
    id VARCHAR(64) NOT NULL COMMENT 'Arxiv ID (e.g., 2310.00123)，作为主键',
    title VARCHAR(255) NOT NULL COMMENT '论文标题',
    abstract TEXT COMMENT '摘要 (TEXT 类型可存 64KB，足够)',
    published_date DATE COMMENT '发布日期 (yyyy-MM-dd)',
    categories VARCHAR(255) COMMENT '分类 (.分隔)',
    url VARCHAR(64) COMMENT 'Arxiv URL 后缀',
    metadata JSON COMMENT '扩展字段：存储 Journal, DOI, License 等，为未来留后路',

    PRIMARY KEY (id),
    -- [G-Arch Note]: 删除 idx_title，搜索走 ES。保留 published_date 用于简单的按日期归档/导出。
    INDEX idx_published_date (published_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论文元数据主表';

-- ==========================================
-- 2. 字典表: authors (简单的去重)
-- ==========================================
CREATE TABLE authors (
    id BIGINT AUTO_INCREMENT NOT NULL COMMENT '自增内部 ID',
    name VARCHAR(64) NOT NULL COMMENT '作者姓名',

    PRIMARY KEY (id),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作者信息表';

-- ==========================================
-- 3. 关联表: paper_authors (多对多关系)
-- ==========================================
CREATE TABLE paper_authors (
    paper_id VARCHAR(64) NOT NULL,
    author_id BIGINT NOT NULL,
    author_order INT DEFAULT 0 COMMENT '作者排序 (第一作者、第二作者...)',

    -- 联合主键，防止同一作者在同一篇论文出现两次
    PRIMARY KEY (paper_id, author_id),
    INDEX idx_author_id (author_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论文-作者关联表';

-- ==========================================
-- 4. 引用关系表: citations (图关系边)
-- ==========================================
CREATE TABLE citations (
    source_paper_id VARCHAR(64) NOT NULL COMMENT '引用方 (Who cites)',
    target_paper_id VARCHAR(64) NOT NULL COMMENT '被引方 (Who is cited)',

    -- 联合主键，防止重复记录同一条引用
    PRIMARY KEY (source_paper_id, target_paper_id),
    INDEX idx_target_paper (target_paper_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='引用关系表 (简化版图)';