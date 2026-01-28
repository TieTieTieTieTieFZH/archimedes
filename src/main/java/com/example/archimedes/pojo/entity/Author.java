package com.example.archimedes.pojo.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("authors")
public class Author implements Serializable {
    
    /**
     * 自增内部 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 作者姓名
     */
    private String name;
}