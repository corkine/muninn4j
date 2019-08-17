package com.mazhangjing.muninn.v1.Service;

import com.mazhangjing.muninn.v1.DAO.VolumeDAO;
import com.mazhangjing.muninn.v1.Entry.Chapter;
import com.mazhangjing.muninn.v1.Entry.PostScript;
import com.mazhangjing.muninn.v1.Entry.Volume;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class VolumeServiceTest {

    VolumeService service = new VolumeService();
    {
        service.setDao(new VolumeDAO());
    }

    @Test
    public void getVolumes() {
        List<Volume> volumes = service.getVolumes();
        System.out.println("volumes = " + volumes);
    }

    @Test
    public void getVolume() {
        Volume b1 = service.getVolume("b1");
        System.out.println("b1 = " + b1);
    }

    @Test
    public void getChapter() {
        Chapter chapter = service.getChapter("b1", "c1");
        System.out.println("chapter = " + chapter);
        if (chapter == null) return;
        Set<PostScript> postscripts = chapter.getPostscripts();
        System.out.println("postscripts = " + postscripts);
    }
}