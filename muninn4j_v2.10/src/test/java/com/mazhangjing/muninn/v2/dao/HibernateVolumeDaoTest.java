package com.mazhangjing.muninn.v2.dao;

import com.mazhangjing.muninn.v2.entry.Chapter;
import com.mazhangjing.muninn.v2.entry.Volume;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class HibernateVolumeDaoTest {

    ApplicationContext context;
    VolumeDao dao;

/*    {
        context = new ClassPathXmlApplicationContext("spring.cfg.xml");
        dao = context.getBean(VolumeDao.class);
    }

    @Test
    public void getVolumes() {
        List<Volume> volumes = dao.getVolumes();
        System.out.println("volumes = " + volumes);
        if( !volumes.isEmpty()) {
            Set<Chapter> chapters = volumes.get(0).getChapters();
            System.out.println("chapters = " + chapters);
        }
    }

    @Test
    public void getVolume() {
    }

    @Test
    public void getChapter() {
    }*/
}