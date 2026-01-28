package com.example.archimedes.pojo.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("citations")
public class Citation implements Serializable {
    
    /**
     * 引用方 (Who cites)
     */
    private String sourcePaperId;
    
    /**
     * 被引方 (Who is cited)
     */
    private String targetPaperId;
}