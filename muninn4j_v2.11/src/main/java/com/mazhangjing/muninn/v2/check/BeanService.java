package com.mazhangjing.muninn.v2.check;

import com.mazhangjing.muninn.v2.entry.Chapter;
import com.mazhangjing.muninn.v2.entry.Volume;

import java.util.List;

public interface BeanService {
    List<Volume> doService(List<Chapter> chapters, Configure configure);
}
