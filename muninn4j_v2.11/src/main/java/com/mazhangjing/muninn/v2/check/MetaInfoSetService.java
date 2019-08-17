package com.mazhangjing.muninn.v2.check;

import com.mazhangjing.muninn.v2.entry.Chapter;
import com.mazhangjing.muninn.v2.entry.Volume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.util.*;

public class MetaInfoSetService implements BeanService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

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
                //注意 null
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
                Map<String, List<Object>> chaptersMap = (Map<String, List<Object>>) volumeBeans.get("chapterFiles");
                chaptersMap.entrySet().stream().forEach(
                        (entrySet) -> {
                            String chapterId = entrySet.getKey();
                            List<Object> chapterList = entrySet.getValue();
                            if (chapterList.size() < 2) {
                                logger.warn("错误的列表长度： " + chapterList);
                                return; }
                            File file = Paths.get(fromPath, fromFolder, chapterList.get(1).toString()).toFile();
                            //如果文件变动和元信息中的预留地址匹配，即我们需要根据元信息，解析此文件以更新数据库
                            if (chapters == null || chapters.isEmpty()) return;
                            else chapters.stream()
                                    .filter(chapter -> chapter != null && chapter.getFileName() != null)
                                    .filter(chapter -> chapter.getFileName().equals(file.getName()))
                                    .forEach(chapter -> {
                                        chapter.setId(chapterId);
                                        //从元信息中更新并且设置Id
                                        Integer order = chapterList.size() <= 2 ? 999 :
                                        Optional.ofNullable(chapterList.get(2)).map(obj -> {
                                            return (int) obj;
                                        }).orElse(999);
                                        chapter.setOrders(order);

                                        //更新并且设置文件地址
                                        String path =
                                                Paths.get(fromFolder, chapterList.get(1).toString()).toString().replace("\\", "/");
                                        chapter.setFileName(path);

                                        //如果获取不到元信息，则添加最基础的标题
                                        if (chapter.getTitle() == null || chapter.getTitle().isEmpty()){
                                            chapter.setTitle(chapterList.get(0).toString());
                                        }

                                        logger.info(String.format("%s is joining Chapter: %s now..."
                                                ,getClass().getSimpleName(),chapter.getTitle()));
                                        //必须互相添加
                                        chapter.setVolume(volume);
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
        if (configType == null) return "com.mazhangjing.muninn.v2.entry.Book";
        switch (configType) {
            case "BOOK":
                return "com.mazhangjing.muninn.v2.entry.Book";
            case "COURSE":
                return "com.mazhangjing.muninn.v2.entry.Course";
            default:
                return "com.mazhangjing.muninn.v2.entry.Book";
        }
    }
}
