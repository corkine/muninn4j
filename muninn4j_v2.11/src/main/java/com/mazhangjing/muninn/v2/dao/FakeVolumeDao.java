package com.mazhangjing.muninn.v2.dao;

import com.mazhangjing.muninn.v2.entry.Book;
import com.mazhangjing.muninn.v2.entry.Chapter;
import com.mazhangjing.muninn.v2.entry.PostScript;
import com.mazhangjing.muninn.v2.entry.Volume;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class FakeVolumeDao implements VolumeDao {

    @Override
    public List<Volume> getVolumes() {
        Volume volume1 = new Book();
        volume1.setCategory("Category1");
        volume1.setId("v1");
        volume1.setIntro("Category1 - volume1 - v1");
        volume1.setLink("http://link.com");
        volume1.setTitle("Volumes1");
        Volume volume2 = new Book();
        volume2.setCategory("Category2");
        volume2.setId("v2");
        volume2.setIntro("Category2 - volume2 - v2");
        volume2.setLink("http://link.com");
        volume2.setTitle("Volumes2");
        return Arrays.asList(volume1,volume2);
    }

    @Override
    public Volume getVolume(String volumeId) {
        Volume volume2 = new Book();
        volume2.setCategory("Category2");
        volume2.setId("v2");
        volume2.setIntro("Category2 - volume2 - v2");
        volume2.setLink("http://link.com");
        volume2.setTitle("Volumes2");
        return volume2;
    }

    @Override
    public Chapter getChapter(String volumeId, String characterId) {
        Chapter chapter = new Chapter();
        chapter.setTitle("Chapter1");
        return chapter;
    }

    @Override
    public void saveOrUpdateVolume(Volume volume) { }

    @Override
    public void saveOrUpdateChapter(Chapter chapter) { }

    @Override
    public void savePostScripts(Chapter chapter, boolean deleteBeforeUpdate) { }

    @Override
    public void deleteChaptersAndPostScriptsByVolume(String volumeId) { }

    @Override
    public void deleteChapterAndPostScripts(String chapterId) {

    }

    @Override
    public List<String> queryAllChapterIdByVolume(String volumeId) {
        return null;
    }

    @Override
    public void addPostScript(String chapterId, PostScript postScript) {

    }

    @Override
    public void deletePostScript(String psId) {

    }

    @Override
    public Integer getPostScriptsCountInChapter(String chapterId) {
        return null;
    }

    @Override
    public List<Chapter> getChapters(Date date) {
        return null;
    }
}
