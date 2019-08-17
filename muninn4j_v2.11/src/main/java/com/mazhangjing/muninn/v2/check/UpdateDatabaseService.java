package com.mazhangjing.muninn.v2.check;

import com.mazhangjing.muninn.v2.dao.JdbcVolumeDao;
import com.mazhangjing.muninn.v2.dao.VolumeDao;
import com.mazhangjing.muninn.v2.entry.Chapter;
import com.mazhangjing.muninn.v2.entry.PostScript;
import com.mazhangjing.muninn.v2.entry.Volume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class UpdateDatabaseService implements UpdateService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void doService(Configure configure, TransService moveService,
                          ParseService parseService, BeanService beanService, VolumeDao dao) {
        //整个Checker 的流程：从全局配置文件加载元信息，构造包含元信息的课程对象（对象现在不包含内部的章节）；
        //之后对新修改的章节进行迁移、文件格式转换，之后从这些HTML文件中提取这些新修改章节的信息，根据元数据，查找到此章节所属
        //的课程，以及确定此章节的 id。并且将其添加到课程对象中。与此同时，删除那些数据库有的，但是文件没有的数据库章节对象，以及其附带的留言信息。
        //现在我们获得的 Volumes 中的对象，将会是所有的 课程对象，但是，对象仅包含更新的信息，也就是说，只有更新的章节在此处
        //才会显示，所以说，我们不能够直接保存课程对象，否则的话，所有的章节信息都会被覆盖。
        logger.info("Getting configure object " + configure);

        logger.info("MoveService is on...");
        List<File> files = moveService.doService();
        List<File> nFiles = files.stream().map(file -> {
            File nFile = Paths.get(file.toString().replace(".ipynb", ".html")).toFile();
            return nFile;
        }).collect(Collectors.toList());

        logger.info("ParseService is on...");
        List<Chapter> chapters = parseService.doService(nFiles);

        logger.info("MetaInfoService is on...");
        List<Volume> volumes = beanService.doService(chapters, configure);

        logger.info("DaoService is on...");
        //每次有文件变动，都会从元数据遍历 volume 信息，重新让元数据和数据库进行一次对齐
        //或者添加一个过滤器，只让有 chapter 的 volume 对齐，只有更新章节，或者新建章节的时候才更新数据库

        //从配置文件中加载处理数据的配置
        boolean[] setting = Optional.ofNullable(configure.getTransConfig())
                .filter(transConfig -> transConfig.get("lazyFetchVolume") != null &&
                        transConfig.get("deletePostScriptBeforeUpdate") != null)
                .map(transConf -> {
                    boolean[] result = new boolean[2];
                    result[0] = transConf.get("lazyFetchVolume").equalsIgnoreCase("true");
                    result[1] = transConf.get("deletePostScriptBeforeUpdate").equalsIgnoreCase("true");
                    logger.info("Getting config: lazyFetchVolume ->" + result[0] + ", deletePostScriptBeforeUpdate -> " + result[1]);
                    return result;
                }).orElse(new boolean[]{true, false});
        boolean lazyFetchVolume = setting[0];
        boolean deletePostScriptBeforeUpdate = setting[1];

        logger.info("Lazy updating volume is " + lazyFetchVolume);

        volumes.stream()
                //根据规则来选择全部 volume 或者有 chapter 需要更新的 volume
                //如果 lazy 更新为 true，则判断是否有章节信息，以及如果存在 list，其长度是否为 0，以此过滤懒更新
                .filter(volume -> {
                    if (lazyFetchVolume && (volume.getChapters() == null || volume.getChapters().isEmpty())) {
                        logger.debug("Lazy updating volume is on，skip sync " + volume.getTitle()); return false;
                    } else { logger.debug("Lazy updating volume is off， Sync " + volume.getTitle());return true; } })
                //如果 volume 没有 id，则无法进行更新, 后续也无法删除残余 Chapter
                .filter(volume -> volume.getId() != null)
                //更新 Volume 到数据库
                .peek(dao::saveOrUpdateVolume)
                //进入 chapter 的更新，首先，在之前的判断中，如果 lazy 关闭，则不判断 chapter 存在，这里还需要再判断一次
                .filter(volume -> volume.getChapters() != null && !volume.getChapters().isEmpty())
                //首先对元信息中没有的 chId 进行清理
                .peek(volume -> {
                    List<String> allChaptersInDB = dao.queryAllChapterIdByVolume(volume.getId());
                    Map<String, Map<String, Object>> volumeInfo = configure.getVolumeInfo();
                    Map<String, Object> volumeMap = volumeInfo.get(volume.getId());
                    Object chapterFiles = volumeMap.get("chapterFiles");
                    List<String> collect = new ArrayList<>();
                    if (chapterFiles == null) return;
                    else {
                        try {
                            Map<String,List<Object>> cf = (Map<String, List<Object>>) chapterFiles;
                            collect = cf.entrySet().stream()
                                    .map(Map.Entry::getKey).filter(Objects::nonNull)
                                    .collect(Collectors.toList());
                        } catch (Exception e) {
                            logger.warn("Can't case chapterFiles to Map<String,List<Object>> " + volume.getTitle());
                        } }
                    logger.info("Chapters in metaInfo is " + collect);
                    final List<String> finalCollect = collect;
                    allChaptersInDB.stream()
                            .filter(o -> !finalCollect.contains(o))
                            .forEach(chapterId -> {
                                logger.info("Deleting chapter: " + chapterId + ", because it don't exist in metaInfo");
                                dao.deleteChapterAndPostScripts(chapterId); }); })
                //接着进入 Chapter 流
                .forEach(volume ->
                        volume.getChapters().stream()
                                //需要判断chapter 的 id，如果没有 id，则无法保存
                                .filter(chapter -> chapter.getId() != null)
                                //更新 Chapter 到数据库
                                .peek(chapter -> { logger.info("Updating chapter " + chapter.getTitle());
                                    dao.saveOrUpdateChapter(chapter); })
                                .filter(chapter -> chapter.getPostscripts() != null && !chapter.getPostscripts().isEmpty())
                                //接下来，更新 postScript，因为 id 是 数据库添加时才加上的（hashCode），因此判断 title 或者 body
                                .forEach(chapter -> {
                                    Set<PostScript> collect =
                                            chapter.getPostscripts().stream()
                                                    .filter(Objects::nonNull)
                                                    .collect(Collectors.toSet());
                                    chapter.setPostscripts(collect);
                                    logger.info("Updating postScript " + chapter.getPostscripts());
                                    //更新 PostScript 到数据库
                                    dao.savePostScripts(chapter,deletePostScriptBeforeUpdate);
                                })
                );
    }
}
