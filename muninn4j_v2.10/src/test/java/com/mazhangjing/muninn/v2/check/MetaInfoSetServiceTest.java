package com.mazhangjing.muninn.v2.check;

import com.mazhangjing.muninn.v2.entry.Chapter;
import com.mazhangjing.muninn.v2.entry.Volume;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class MetaInfoSetServiceTest {

    private MetaInfoSetService service = new MetaInfoSetService();

    /*@Test public void test() throws Exception {
        Yaml yaml = new Yaml();
        Configure configure = yaml.loadAs(
                new FileReader("C:\\Users\\Corkine\\muninn4j_v2\\src\\main\\resources\\meta.yml"),
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
            String clazz = service.whichCategory((String) volumeBeans.get("type"));
            Volume volume = (Volume) Class.forName(clazz).newInstance();
            volume.setCategory(category);
            volume.setTitle(title);
            volume.setId(volumeId);
            volume.setIntro(intro);
            volumeList.add(volume);

            service.logger.info(String.format("%s is building Volume: %s now..."
                    ,getClass().getSimpleName(),volume.getTitle()));

            String fromFolder = (String) volumeBeans.get("fromFolder");

            //从配置文件构建 Chapter，结合 Parse 得到的 Chapter 信息，整合 Volume 和 Chapter
            Map<String, List<String>> chaptersMap = (Map<String, List<String>>) volumeBeans.get("chapterFiles");
            if (chaptersMap != null){
                chaptersMap.entrySet().stream().forEach(
                        (keyvaule) -> {
                            String chapterId = keyvaule.getKey();
                            List<String> chapterList = keyvaule.getValue();
                            if (chapterList.size() < 2) {
                                service.logger.warn("错误的 List 参数长度: " + chapterList);
                                return; }
                            String chapterAddress = chapterList.get(1);
                            File file = Paths.get(fromPath, fromFolder, chapterAddress).toFile();
                            chapters.stream()
                                    //如果有更新的章节和我们元信息中的课程的章节信息由相同的地址，则判断为同一个章节，需要
                                    //更新此章节的信息到数据库，因此，为此章节添加 id 属性，之后添加到课程，以备更新数据库
                                    .filter(chapter -> chapter.getFileName().equals(file.getName()))
                                    .forEach(chapter -> {
                                        chapter.setId(chapterId);

                                        service.logger.info(String.format("%s is joining Chapter: %s now..."
                                                ,getClass().getSimpleName(),chapter.getTitle()));

                                        volume.getChapters().add(chapter);
                                    });
                        }
                );
            }
        }
        System.out.println("chapters = " + chapters);
        System.out.println("volumeList = " + volumeList);
        if (volumeList.size() > 1) {
            Set<Chapter> chapters1 = volumeList.get(0).getChapters();
            System.out.println("chapters1 = " + chapters1);}
    }*/

}