package com.mazhangjing.muninn.checker;

import com.mazhangjing.muninn.v1.Entry.Chapter;

import java.io.File;
import java.util.List;

public interface ParseService {

    List<Chapter> doService(List<File> needParseHtmlFiles);
}
