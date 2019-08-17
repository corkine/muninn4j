package com.mazhangjing.muninn.v1.Service;

import com.mazhangjing.muninn.v1.DAO.VolumeDAO;
import com.mazhangjing.muninn.v1.Entry.Chapter;
import com.mazhangjing.muninn.v1.Entry.Volume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VolumeService {

    private VolumeDAO dao;

    @Autowired public void setDao(VolumeDAO dao) { this.dao = dao; }

    public List<Volume> getVolumes() {
        return dao.getVolumes();
    }

    public Volume getVolume(String volumeId) {
        return dao.getVolume(volumeId);
    }

    public Chapter getChapter(String volumeId, String chapterId) {
        return dao.getChapter(volumeId,chapterId);
    }

}
