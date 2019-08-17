package com.mazhangjing.muninn.v2.check;

import com.mazhangjing.muninn.v2.entry.Chapter;

import java.io.File;
import java.util.List;

public interface ParseService {

    List<Chapter> doService(List<File> needParseHtmlFiles);
}
