package com.mazhangjing.muninn.checker;

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
    private List<Path> allowedPath;

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
                (volumeId, volumeBean) -> {
                    Path volumeFolder = Paths.get((String) volumeBean.get("fromFolder"));
                    Map<String,String> chapterFiles = (Map<String, String>) volumeBean.get("chapterFiles");
                    chapterFiles.forEach(
                            (chapterId, chapterFile) -> {
                                Path chapterPath = Paths.get(chapterFile.replace(".html",".ipynb"));
                                Path realPath = Paths.get(source.toString(),volumeFolder.toString(),chapterPath.toString());
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
