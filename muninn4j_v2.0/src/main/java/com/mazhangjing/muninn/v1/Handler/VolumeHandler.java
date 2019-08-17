package com.mazhangjing.muninn.v1.Handler;

import com.mazhangjing.muninn.v1.Entry.Chapter;
import com.mazhangjing.muninn.v1.Entry.Volume;
import com.mazhangjing.muninn.v1.Service.VolumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping(value = "/")
public class VolumeHandler {

    private VolumeService service;

    @Autowired public void setService(VolumeService service) { this.service = service;}

    @RequestMapping("/")
    public String goHome(Map<String, Map<String, List<Volume>>> map) {
        map.put("header",getHeader());
        return "index";
    }

    @RequestMapping("/volume/{volume_id}")
    public String goVolumeDetail(@PathVariable("volume_id") String volumeId,
                                 Map<String, Object> map) {
        map.put("volume",service.getVolume(volumeId));
        map.put("header",getHeader());
        return "volume";
    }

    @RequestMapping("/volume/{volume_id}/chapter/{chapter_id}")
    public String goChapterDetail(@PathVariable("chapter_id") String chapterId,
                                  @PathVariable("volume_id") String volumeId,
                                  Map<String, Object> map) {
        map.put("chapter",service.getChapter(volumeId,chapterId));
        map.put("volume",service.getVolume(volumeId));
        map.put("header",getHeader());
        return "chapter";
    }

    private Map<String, List<Volume>> getHeader() {
        try {
            Map<String, List<Volume>> collect = service.getVolumes().stream()
                    .collect(Collectors.groupingBy(Volume::getCategory));
            System.out.println("collect = " + collect);
            return collect;
        } catch (Exception e) {
            e.printStackTrace();
        } return null;
    }

    private List<List<List<String>>> getInfo(String volId, String chId) {
        List<List<List<String>>> result = new ArrayList<>();
        try {
            Chapter chapter = service.getChapter(volId, chId);
            String content = chapter.getContent();
            result = Arrays.stream(
                    content.split("\t\t\t")).filter(p -> !p.isEmpty())
                    .map((h1) -> Arrays.stream(h1.split("\t\t")).filter(p -> !p.isEmpty())
                    .map((h2) -> Arrays.stream(h2.split("\t")).filter(p -> !p.isEmpty())
                    .collect(Collectors.toList())).collect(Collectors.toList())).collect(Collectors.toList());
        } catch (Exception e) {}
        return result;
    }
}
