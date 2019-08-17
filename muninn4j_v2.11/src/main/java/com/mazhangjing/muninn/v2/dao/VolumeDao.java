package com.mazhangjing.muninn.v2.dao;

import com.mazhangjing.muninn.v2.entry.Chapter;
import com.mazhangjing.muninn.v2.entry.PostScript;
import com.mazhangjing.muninn.v2.entry.Volume;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

public interface VolumeDao {

    /**获取所有的课程信息，不包括其包含的所有章节的的信息*/
    List<Volume> getVolumes();

    /**获取单个课程的信息，包括其包含的所有章节的信息，但是不包括章节对象的 Set 信息*/
    Volume getVolume(String volumeId);

    /**获得单个章节的信息，包括其所有评论和笔记信息*/
    Chapter getChapter(String volumeId, String characterId);

    /**JDBCDao 专用，用来更新 Volume 的接口，不保存其 Chapter 的 set 容器**/
    void saveOrUpdateVolume(Volume volume);

    /**JDBCDao 专用，用来保存/更新 Chapters 的接口，不保存 PostScripts 容器*/
    void saveOrUpdateChapter(Chapter chapter);

    /**JDBCDao 专用，用来保存 Chapter 的 PostScripts 容器*/
    void savePostScripts(Chapter chapter, boolean deleteBeforeUpdate);

    void deleteChaptersAndPostScriptsByVolume(String volumeId);

    void deleteChapterAndPostScripts(String chapterId);

    List<String> queryAllChapterIdByVolume(String volumeId);

    void addPostScript(String chapterId, PostScript postScript);

    void deletePostScript(String psId);

    Integer getPostScriptsCountInChapter(String chapterId);

    /**
     * 获取指定日期的 Chapter，不包括 Volume 但是包括 PostScript
     */
    List<Chapter> getChapters(Date date);
}

