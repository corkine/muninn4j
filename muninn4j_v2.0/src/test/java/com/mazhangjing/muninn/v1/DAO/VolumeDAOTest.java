package com.mazhangjing.muninn.v1.DAO;

import com.mazhangjing.muninn.v1.Entry.Chapter;
import com.mazhangjing.muninn.v1.Entry.Volume;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class VolumeDAOTest {

    VolumeDAO dao = new VolumeDAO();

    @Test
    public void getVolumes() {
        List<Volume> volumes = dao.getVolumes();
        System.out.println("volumes = " + volumes);
    }

    @Test
    public void getVolume() {
        Volume b1 = dao.getVolume("b1");
        System.out.println("b1 = " + b1);
    }

    @Test
    public void getChapter() {
        Chapter chapter = dao.getChapter("b1", "c1");
        System.out.println("chapter = " + chapter == null);
    }
}