package com.example.archimedes.pojo.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("paper_authors")
public class PaperAuthor implements Serializable {
    
    /**
     * 论文ID
     */
    private String paperId;
    
    /**
     * 作者ID
     */
    private Long authorId;
    
    /**
     * 作者排序 (第一作者、第二作者...)
     */
    private Integer authorOrder;
}