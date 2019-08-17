package com.mazhangjing.muninn.v2.check;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class NoteBookMoveConfig {

    private String scanFreq;
    private String command;
    private Path target;
    private Path source;
    private List<Path> allowedPath = new ArrayList<>();

    public List<Path> getAllowedPath() {
        return allowedPath;
    }

    public void setAllowedPath(List<Path> allowedPath) {
        this.allowedPath = allowedPath;
    }

    public NoteBookMoveConfig(boolean fromProperties) throws IOException { readProperties(); }

    public NoteBookMoveConfig(Configure configure) {
        settingConfig(configure);
    }

    private void readProperties() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("notebook.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        source = Paths.get(properties.getProperty("notebook.fromPath"));
        target = Paths.get(properties.getProperty("notebook.toPath"));
        scanFreq = properties.getProperty("notebook.scanFreq");
        command = properties.getProperty("notebook.transCommand");
        inputStream.close();
    }

    private void settingConfig(Configure configure) {
        source = Paths.get(configure.getTransConfig().get("fromPath"));
        target = Paths.get(configure.getTransConfig().get("toPath"));
        command = configure.getTransConfig().get("transCommand");
        allowedPath = new ArrayList<>();
        configure.getVolumeInfo().forEach(
                (volumeId, volumeMap) -> {
                    Path volumeFolder = Paths.get((String) volumeMap.get("fromFolder"));
                    Map<String, List<String>> chapterFiles = (Map<String, List<String>>) volumeMap.get("chapterFiles");
                    //可能不存在此列表，可能存在此列表，但为空
                    if (chapterFiles == null) return;
                    chapterFiles.forEach(
                            (chapterId, chapterList) -> {
                                //如果不存在列表，或者参数不足，跳过，否则，得到章节对应的 ipynb 文件 URL
                                if (chapterList == null || chapterList.size() < 2) return;
                                Path chapterPath = Paths.get(chapterList.get(1).replace(".html",".ipynb"));
                                Path realPath = Paths.get(target.toString(),volumeFolder.toString(),chapterPath.toString());
                                allowedPath.add(realPath);
                            }
                    );
                }
        );
    }

    public String getCommand() { return command; }

    public String getScanFreq() {
        return scanFreq;
    }

    public Path getTarget() {
        return target;
    }

    public Path getSource() {
        return source;
    }
}
