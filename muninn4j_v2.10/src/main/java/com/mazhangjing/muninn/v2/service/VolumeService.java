package com.mazhangjing.muninn.v2.service;

import com.mazhangjing.muninn.v2.dao.HibernateVolumeDao;
import com.mazhangjing.muninn.v2.dao.VolumeDao;
import com.mazhangjing.muninn.v2.entry.Chapter;
import com.mazhangjing.muninn.v2.entry.PostScript;
import com.mazhangjing.muninn.v2.entry.Volume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VolumeService {

    private VolumeDao volumeDao;

    @Autowired
    public void setVolumeDao(VolumeDao volumeDao) { this.volumeDao = volumeDao; }

    public List<Volume> getVolumes() {
        return volumeDao.getVolumes();
    }

    public int getPostScriptCountInChapter(String chapterId) {
        return volumeDao.getPostScriptsCountInChapter(chapterId); }

    public Volume getVolume(String volumeId) {
        return volumeDao.getVolume(volumeId);
    }

    public Chapter getChapter(String volumeId, String chapterId) {
        return volumeDao.getChapter(volumeId,chapterId);
    }

    public Map<String, List<Volume>> getHeader() {
        try {
            Map<String, List<Volume>> collect = getVolumes().stream()
                    .filter(volume -> volume.getCategory() != null)
                    .collect(Collectors.groupingBy(Volume::getCategory));
            collect.forEach((category, cList) -> cList.sort(Comparator.comparing(Volume::getTitle)));
            return collect;
        } catch (Exception e) {
            e.printStackTrace();
        } return null;
    }

    public void deletePostScript(String volumeId, String chapterId, String psId) {
        volumeDao.deletePostScript(psId);
    }

    public void addPostScript(String chapterId, PostScript postScript) {
        volumeDao.addPostScript(chapterId, postScript);
    }

    /**
     * 获取当日的所有笔记，DAO 层不会抛出异常，如果没有，返回空列表。
     * @return 当日所有章节信息，包含其 PostScripts 信息，不包含 Volume 信息。
     */
    public List<Chapter> getTodayChapters() {
        return volumeDao.getTodayChapters();
    }

}
