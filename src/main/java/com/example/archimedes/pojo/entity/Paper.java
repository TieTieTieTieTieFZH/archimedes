package com.example.archimedes.pojo.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDate;
import java.util.Map;

@Data
@Accessors(chain = true)
@TableName(value = "papers", autoResultMap = true)
public class Paper implements Serializable {
    
    /**
     * Arxiv ID (e.g., 2310.00123)，作为主键
     */
    @TableId(type = IdType.INPUT)
    private String id;
    
    /**
     * 论文标题
     */
    private String title;
    
    /**
     * 摘要
     */
    @TableField(value = "abstract")
    private String abstractText;
    
    /**
     * 发布日期 (yyyy-MM-dd)
     */
    @TableField("published_date")
    private LocalDate publishedDate;
    
    /**
     * 分类 (.分隔)
     */
    private String categories;
    
    /**
     * Arxiv URL 后缀
     */
    private String url;
    
    /**
     * 向量嵌入 - 存储为JSON格式
     * MySQL中存储为LONGTEXT，ES中为dense_vector
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private float[] embedding;
    
    /**
     * 扩展字段：存储 Journal, DOI, License 等
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> metadata;
}