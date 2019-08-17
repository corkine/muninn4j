package com.mazhangjing.muninn.v2.check;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JupyterNoteBookMoveService implements TransService{

    private static JupyterNoteBookMoveService instance;

    private Logger logger = LoggerFactory.getLogger(JupyterNoteBookMoveService.class);

    private NoteBookMoveConfig config;

    private JupyterNoteBookMoveService(NoteBookMoveConfig config) { this.config = config; }

    public static JupyterNoteBookMoveService getService(boolean useConfigure, Configure configure) {
        if (instance == null) {
            if (useConfigure) instance = new JupyterNoteBookMoveService(new NoteBookMoveConfig(configure));
            else {
                try { instance = new JupyterNoteBookMoveService(new NoteBookMoveConfig(true));
                } catch (IOException e) { e.printStackTrace(); }
            }
        }
        return instance;
    }

    private void moveFiles() {
        logger.info("Starting " + getClass().getSimpleName() + " Moving files...");

        try {
            Files.walkFileTree(config.getSource(), new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    logger.debug("preVisit DIR " + dir);
                    Path targetdir = config.getTarget().resolve(config.getSource().relativize(dir));
                    try {
                        Files.copy(dir, targetdir);
                    } catch (FileAlreadyExistsException e) {
                        if (!Files.isDirectory(targetdir))
                            throw e;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    //对于所有文件夹的文件都需要复制，但是仅对那些 .ipynb 进行转换
                    /*if (!config.getAllowedPath().contains(file)) return FileVisitResult.CONTINUE;
                    logger.debug("Find ipynb files We need..." + file);*/
                    Path tPath = config.getTarget().resolve(config.getSource().relativize(file));
                    File tfile = tPath.toFile();
                    File rfile = file.toFile();
                    if (tfile.exists() && tfile.isFile() && rfile.isFile()) {
                        if (tfile.lastModified() < rfile.lastModified()) {
                            logger.info("Updating files:" + file);
                            Files.copy(file, tPath,StandardCopyOption.REPLACE_EXISTING);
                        }
                    } else if (!tfile.exists()) {
                        Files.copy(file, tPath);
                        logger.info("Creating files:" + file);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void executeCommand(Path fileNeedTrans) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        String command = config.getCommand();
        Process process;
        if (System.getProperty("os.name").toUpperCase().contains("WINDOWS")) {
            String realWindowsCommand = "cmd /c " + command + " " + fileNeedTrans.toString();
            logger.info("Execute: " + realWindowsCommand);
            process = runtime.exec(realWindowsCommand);
        } else {
            String[] cmd = {"/bin/bash","-c",config.getCommand() + " " + fileNeedTrans.toString()};
            logger.info("Execute: " + Arrays.asList(cmd));
            process = runtime.exec(cmd);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder sb = new StringBuilder(); String message;
        while ((message = reader.readLine()) != null) {
            sb.append(message);
        }
        if (sb.toString().isEmpty()) logger.info("Trans successful!"); else logger.info(sb.toString());
        reader.close();
    }

    private List<Path> collectingTransFiles() {
        List<Path> fileNeedTrans = new ArrayList<>();
        logger.info("Starting " + getClass().getSimpleName() + " Collecting Trans files...");

        Path source = config.getTarget();

        try {
            Files.walkFileTree(source, new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) { return FileVisitResult.CONTINUE; }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    //对于所有文件夹的文件都需要复制，但是仅对那些 .ipynb 进行转换
                    //绝对路径的比较, 避免处理大量的 checkpoint 文件夹下的 .ipynb 文件
                    if (!config.getAllowedPath().contains(file)) return FileVisitResult.CONTINUE;
                    logger.debug("Find ipynb files We need..." + file);

                    //如果文件不以 .ipnb 结尾，则跳过遍历
                    if (!file.toString().endsWith(".ipynb")) return FileVisitResult.CONTINUE;
                    //如果文件以 .ipynb 结尾，进行判断
                    File iPynbFile = file.toFile();
                    File htmlFile = Paths.get(file.toString().replace(".ipynb",".html")).toFile();
                    //如果文件存在，并且对应的 html 文件存在，并且 html 文件较新，则不进行转换，否则进行转换
                    if (iPynbFile.exists() && iPynbFile.isFile()
                        && htmlFile.exists() && htmlFile.isFile()
                        && htmlFile.lastModified() > iPynbFile.lastModified()) return FileVisitResult.CONTINUE;
                    fileNeedTrans.add(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) { return FileVisitResult.CONTINUE; }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) { return FileVisitResult.CONTINUE; }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!fileNeedTrans.isEmpty()) logger.info("Get files: " + fileNeedTrans);
        return fileNeedTrans;
    }

    private List<File> transAllFiles(List<Path> fileNeedTrans) {
        List<File> htmlNeedParse = new ArrayList<>();
        if (fileNeedTrans.isEmpty()) return htmlNeedParse;
        fileNeedTrans.stream().parallel().forEach((path -> {
                    try { executeCommand(path); } catch (IOException e) {
                        StringWriter writer = new StringWriter();
                        e.printStackTrace(new PrintWriter(writer));
                        logger.error(writer.toString());
                    } })
        );
        return fileNeedTrans.stream().map(Path::toFile).collect(Collectors.toList());
    }

    public List<File> doService() {
        moveFiles();
        return transAllFiles(collectingTransFiles());
    }
}
