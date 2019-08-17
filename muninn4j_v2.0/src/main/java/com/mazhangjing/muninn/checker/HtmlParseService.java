package com.mazhangjing.muninn.checker;

import com.mazhangjing.muninn.v1.Entry.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;

import java.util.stream.Collectors;

import static java.lang.Class.*;

public class HtmlParseService implements ParseService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<Element> getContent(File file, Supplier<String> cssQuery) throws IOException {
        FileReader reader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String s; StringBuilder builder = new StringBuilder();
        while ((s = bufferedReader.readLine()) != null) { builder.append(s); }
        Document parse = Jsoup.parse(builder.toString());
        Elements select = parse.select(cssQuery.get());
        List<Element> collect = select.stream()
                .map(element -> element.text(element.text().trim().replace("¶", "")))
                .collect(Collectors.toList());
        bufferedReader.close(); reader.close();
        return collect;
    }

    private List<String> getMetaInfo(File file, Supplier<String> introCssQuery) throws IOException {
        FileReader reader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String s; StringBuilder builder = new StringBuilder();
        while ((s = bufferedReader.readLine()) != null) { builder.append(s); }
        Document parse = Jsoup.parse(builder.toString());

        Elements select = parse.select(introCssQuery.get());
        String intro = select.stream().map(element -> element.text().trim().replace("\n", "。"))
                .collect(Collectors.joining("\n"));
        Element title = parse.getElementsByAttributeValue("type", "title").first();
        Element update = parse.getElementsByAttributeValue("type","update").first();
        Element version = parse.getElementsByAttributeValue("type", "version").first();
        String titleS = title.text().trim();
        String updateS = update.text().trim();
        String versionS = version.text().trim();
        List<String> meta = new ArrayList<>
                (Arrays.asList(titleS,updateS,versionS,intro));
        bufferedReader.close(); reader.close();
        return meta;
    }

    private PostScript getPostScript(Map<String,String> map) {
        String mold = map.get("mold");
        String clazz = null;
        try {
            if (mold.equalsIgnoreCase("question")) {
                clazz = "com.mazhangjing.muninn.v1.Entry.Question";
                Question question = (Question) forName(clazz).newInstance();
                question.setTitle(map.get("text"));
                question.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(map.get("update")));
                return question;
            } else if (mold.equalsIgnoreCase("quote")){
                clazz = "com.mazhangjing.muninn.v1.Entry.Quote";
                Quote quote = (Quote) forName(clazz).newInstance();
                quote.setTitle(map.get("text"));
                quote.setFooter(map.get("footer"));
                quote.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(map.get("update")));
                return quote;
            } else if (mold.equalsIgnoreCase("explain")) {
                clazz = "com.mazhangjing.muninn.v1.Entry.Note";
                Note note = (Note) forName(clazz).newInstance();
                note.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(map.get("update")));
                note.setBody(map.get("text"));
                return note;
            } else if (mold.equalsIgnoreCase("blog")) {
                clazz = "com.mazhangjing.muninn.v1.Entry.Link";
                Link link = (Link) forName(clazz).newInstance();
                link.setTime((new SimpleDateFormat("yyyy-MM-dd")).parse(map.get("update")));
                link.setUrl(map.get("url"));
                link.setTitle(map.get("text"));
                link.setBody(map.get("description"));
                return link;
            }
            else return null;
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | ParseException e) {
            e.printStackTrace();
        } return null;
    }

    private List<PostScript> getPostScripts(File file) throws IOException {
        List<PostScript> result = new ArrayList<>();

        FileReader reader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String s; StringBuilder builder = new StringBuilder();
        while ((s = bufferedReader.readLine()) != null) { builder.append(s); }
        Document parse = Jsoup.parse(builder.toString());
        Elements selects = parse.getElementsByAttributeValue("type","related_note");
        selects.stream().forEach(
                select -> {
                    //question, quote, explain, blog
                    Map<String,String> metaData = new HashMap<>();
                    metaData.put("mold",select.attr("mold"));
                    metaData.put("update",select.attr("update"));
                    metaData.put("footer",select.attr("footer"));
                    metaData.put("description",select.attr("description"));
                    metaData.put("url",select.attr("url"));
                    metaData.put("text",select.text());
                    PostScript post = getPostScript(metaData);

                    logger.info(String.format("%s converting PostScript %s now...",
                            getClass().getSimpleName(),post.getClass().getSimpleName()));
                    result.add(post);
                }
        );
        bufferedReader.close(); reader.close();
        return result;
    }

    @Override
    public List<Chapter> doService(List<File> needParseHtmlFiles) {
        if (needParseHtmlFiles.isEmpty())
            logger.info(String.format("%s can't find file need to parse, skipping...",getClass().getSimpleName()));
        else
            logger.info(String.format("%s is Running...",getClass().getSimpleName()));
        return needParseHtmlFiles
                .parallelStream()
                .filter(file -> {
                    boolean res = file.exists() && file.isFile() && file.canRead();
                    if (!res) logger.warn(String.format("%s Error: %s can't read/exist"
                            ,getClass().getSimpleName(),file));
                    return res;
                })
                .map(this::doParse).collect(Collectors.toList());
    }

    private Chapter doParse(File file) {
        Chapter chapter = null;
        try {
            List<Element> content = getContent(file, () -> "h1,h2,h3");
            List<String> metaInfo = getMetaInfo(file, () -> "p.card-text");
            List<PostScript> scripts = getPostScripts(file);
            chapter = new Chapter();
            chapter.setTitle(metaInfo.get(0));
            chapter.setLastEdit(new SimpleDateFormat("yyyy-MM-dd").parse(metaInfo.get(1)));
            chapter.setVersion(metaInfo.get(2));
            chapter.setIntro(metaInfo.get(3));
            chapter.setContent(
                    content.stream()
                           .map(element -> element.toString())
                           .collect(Collectors.joining("\n"))
            );
            chapter.setFileName(file.getName());
            chapter.setNoteCount(Integer.valueOf(chapter.getVersion().split("\\.")[0]));
            chapter.setEditCount(Integer.valueOf(chapter.getVersion().split("\\.")[1]));
            chapter.setPostscripts(new LinkedHashSet<>(scripts));
            logger.info(String.format("%s get POJO: %s",getClass().getSimpleName(),chapter));
        } catch (IOException | ParseException e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            logger.error(writer.toString());
        }
        return chapter;
    }

    @Test public void test() {
        doParse(new File(
                "C:\\Users\\Corkine\\Documents\\工作文件夹\\muninn4j\\src\\main\\resources\\chapter1_play_java.html"));

    }
}
