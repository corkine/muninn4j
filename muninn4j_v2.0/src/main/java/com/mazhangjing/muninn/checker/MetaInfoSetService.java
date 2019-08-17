package com.mazhangjing.muninn.checker;

import com.mazhangjing.muninn.v1.Entry.Chapter;
import com.mazhangjing.muninn.v1.Entry.Volume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class MetaInfoSetService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test public void test() throws Exception {
        Yaml yaml = new Yaml();
        Configure configure = yaml.loadAs(
                new FileReader("C:\\Users\\Corkine\\Documents\\工作文件夹\\muninn4j\\src\\main\\resources\\meta.yml"),
                Configure.class);
        String fromPath = configure.getTransConfig().get("fromPath");
        String toPath = configure.getTransConfig().get("toPath");
        String transCommand = configure.getTransConfig().get("transCommand");
        List<String> volumeOrder = configure.getVolumeOrder();
        Map<String, Map<String,Object>> volumeInfo = configure.getVolumeInfo();

        List<File> needParse = new ArrayList<>();
        needParse.add(new File(
                "C:\\Users\\Corkine\\Documents\\工作文件夹\\muninn4j" +
                        "\\src\\main\\resources\\chapter1_play_java.html"));
        List<Chapter> chapters = new HtmlParseService().doService(needParse);

        List<Volume> volumeList = new ArrayList<>();

        for (Map.Entry<String, Map<String, Object>> entry : volumeInfo.entrySet()) {
            String volumeId = entry.getKey();
            Map<String, Object> volumeBeans = entry.getValue();
            //从配置文件构建 Volume
            String category = (String) volumeBeans.get("category");
            String title = (String) volumeBeans.get("title");
            String intro = (String) volumeBeans.get("intro");
            String clazz = whichCategory((String) volumeBeans.get("type"));
            Volume volume = (Volume) Class.forName(clazz).newInstance();
            volume.setCategory(category);
            volume.setTitle(title);
            volume.setId(volumeId);
            volume.setIntro(intro);
            volumeList.add(volume);

            logger.info(String.format("%s is building Volume: %s now..."
                            ,getClass().getSimpleName(),volume.getTitle()));

            String fromFolder = (String) volumeBeans.get("fromFolder");

            //从配置文件构建 Chapter，结合 Parse 得到的 Chapter 信息，整合 Volume 和 Chapter
            Map<String, String> chaptersMap = (Map<String, String>) volumeBeans.get("chapterFiles");
            chaptersMap.forEach(
                    (chapterId, chapterAddress) -> {
                        File file = Paths.get(fromPath, fromFolder, chapterAddress).toFile();
                        chapters.stream()
                                .filter(chapter -> chapter.getFileName().equals(file.getName()))
                                .forEach(chapter -> {
                                    chapter.setId(chapterId);

                                    logger.info(String.format("%s is joining Chapter: %s now..."
                                            ,getClass().getSimpleName(),chapter.getTitle()));

                                    volume.getChapters().add(chapter);
                                });
                    }
            );
        }
        System.out.println("chapters = " + chapters);
        System.out.println("volumeList = " + volumeList);
        Set<Chapter> chapters1 = volumeList.get(0).getChapters();
        System.out.println("chapters1 = " + chapters1);
    }

    public List<Volume> doService(List<Chapter> chapters, Configure configure) {

        String fromPath = configure.getTransConfig().get("fromPath");
        String toPath = configure.getTransConfig().get("toPath");
        String transCommand = configure.getTransConfig().get("transCommand");
        List<String> volumeOrder = configure.getVolumeOrder();
        Map<String, Map<String,Object>> volumeInfo = configure.getVolumeInfo();

        List<Volume> volumeList = new ArrayList<>();

        for (Map.Entry<String, Map<String, Object>> entry : volumeInfo.entrySet()) {

            try {
                String volumeId = entry.getKey();
                Map<String, Object> volumeBeans = entry.getValue();
                //从配置文件构建 Volume
                String category = (String) volumeBeans.get("category");
                String title = (String) volumeBeans.get("title");
                String intro = (String) volumeBeans.get("intro");
                String clazz = whichCategory((String) volumeBeans.get("type"));
                Volume volume = (Volume) Class.forName(clazz).newInstance();

                volume.setCategory(category);
                volume.setTitle(title);
                volume.setId(volumeId);
                volume.setIntro(intro);
                volumeList.add(volume);

                logger.info(String.format("%s is building Volume: %s now..."
                        ,getClass().getSimpleName(),volume.getTitle()));

                String fromFolder = (String) volumeBeans.get("fromFolder");

                //从配置文件构建 Chapter，结合 Parse 得到的 Chapter 信息，整合 Volume 和 Chapter
                Map<String, String> chaptersMap = (Map<String, String>) volumeBeans.get("chapterFiles");
                chaptersMap.forEach(
                        (chapterId, chapterAddress) -> {
                            File file = Paths.get(fromPath, fromFolder, chapterAddress).toFile();
                            chapters.stream()
                                    .filter(chapter -> chapter.getFileName().equals(file.getName()))
                                    .forEach(chapter -> {
                                        chapter.setId(chapterId);

                                        logger.info(String.format("%s is joining Chapter: %s now..."
                                                ,getClass().getSimpleName(),chapter.getTitle()));

                                        volume.getChapters().add(chapter);
                                    });
                        }
                );
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                StringWriter writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                logger.error(writer.toString());
            }
        }
        return volumeList;
    }

    private String whichCategory(String configType) {
        switch (configType) {
            case "BOOK":
                return "com.mazhangjing.muninn.v1.Entry.Book";
            case "COURSE":
                return "com.mazhangjing.muninn.v1.Entry.Course";
            default:
                return null;
        }
    }
}
