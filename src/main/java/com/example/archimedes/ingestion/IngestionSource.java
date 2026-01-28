package com.example.archimedes.ingestion;

import java.util.Iterator;
import com.example.archimedes.dto.internal.RawPaper;

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