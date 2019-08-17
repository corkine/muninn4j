package com.mazhangjing.muninn.v2.dao;

import com.mazhangjing.muninn.v2.entry.Book;
import com.mazhangjing.muninn.v2.entry.Chapter;
import com.mazhangjing.muninn.v2.entry.Link;
import com.mazhangjing.muninn.v2.entry.Volume;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class JdbcVolumeDaoTest {

    JdbcVolumeDao dao = new JdbcVolumeDao();

/*
    @Test
    public void test1() throws SQLException {
        Volume map = new Book();
        map.setId("dd34");
        map.setTitle("title2");
        map.setIntro("intro");
        map.setCategory("category");
        dao.saveOrUpdateVolume(map);
    }

    @Test
    public void test2() throws SQLException {
        Chapter ch = new Chapter();
        ch.setId("ch2");
        Volume map = new Book();
        map.setId("dd34");
        map.setTitle("title2");
        map.setIntro("intro");
        map.setCategory("category");
        ch.setVolume(map);
        ch.setTitle("title2");
        dao.saveOrUpdateChapter(ch);
    }

    @Test
    public void test3() {
        Chapter ch = new Chapter();
        ch.setId("ch2");
        Link link = new Link();
        link.setTitle("hi");
        link.setUrl("something");
        link.setBody("oddd");
        ch.getPostscripts().add(link);
        dao.savePostScripts(ch,true);
    }

    @Test
    public void test4() {
        dao.queryAllChapterIdByVolume("lang_c");
    }

    @Test
    public void test5() {
        dao.deleteChaptersAndPostScriptsByVolume("model_thinking");
    }

    @Test
    public void test6() {
        dao.deleteChapterAndPostScripts("sew3");
    }
*/
}