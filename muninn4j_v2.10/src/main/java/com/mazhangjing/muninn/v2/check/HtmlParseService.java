package com.mazhangjing.muninn.v2.check;

import com.mazhangjing.muninn.v2.entry.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.lang.Class.forName;

public class HtmlParseService implements ParseService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public Document parse;

    private String fileName;

    //获取目录结构
    private List<Element> getContent(Supplier<String> cssQuery){

        logger.info("Getting content for " + fileName);

        Elements select = parse.select(cssQuery.get());
        return select.stream()
                .filter(element -> element.text() != null)
                .map(element -> element.text(element.text().trim().replace("¶", "")))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    //获取标题、更新和版本信息
    //如果标题、更新、版本任意为空，则不返回任何数据
    private List<String> getMetaInfo(Supplier<String> introCssQuery) {

        logger.info("Getting meta Info query for " + fileName);

        Elements select =
                Optional.ofNullable(parse).map(document -> document.select(introCssQuery.get())).orElse(null);
        Element title =
                Optional.ofNullable(parse)
                .map(document -> document.getElementsByAttributeValue("type","title")).map(Elements::first)
                .orElse(null);
        Element update = Optional.ofNullable(parse)
                .map(document -> document.getElementsByAttributeValue("type", "update"))
                .map(Elements::first).orElse(null);
        Element version =
                Optional.ofNullable(parse).map(document -> document.getElementsByAttributeValue("type","version")).map(Elements::first)
                .orElse(null);

        String intro =  select.stream()
                .filter(element -> element.text() != null)
                .map(element -> element.text().trim().replace("\n","。"))
                .collect(Collectors.joining("\n"));

        String titleS = (title != null? title.text() : null);
        String updateS = (update != null? update.text() : null);
        String versionS = (version != null? version.text() : null);

        if (titleS == null || updateS == null || versionS == null) {
            logger.warn("Can't resolve meta data for " + fileName);
            return new ArrayList<>();
        }
        else return new ArrayList<>(Arrays.asList(titleS,updateS,versionS,intro));
    }

    //通过反射创建 PostScript 对象
    private PostScript getPostScript(Map<String, String> map) {
        String mold = map.get("mold");
        String date = map.get("update");
        String clazz = null;
        try {
            if (mold.equalsIgnoreCase("question")) {
                clazz = "com.mazhangjing.muninn.v2.entry.Question";
                Question question = (Question) forName(clazz).newInstance();
                question.setTitle(map.get("text"));
                if (date != null && !date.isEmpty())
                    question.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(date));
                return question;
            } else if (mold.equalsIgnoreCase("quote")){
                clazz = "com.mazhangjing.muninn.v2.entry.Quote";
                Quote quote = (Quote) forName(clazz).newInstance();
                quote.setTitle(map.get("text"));
                quote.setFooter(map.get("footer"));
                if (date != null && !date.isEmpty())
                    quote.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(date));
                return quote;
            } else if (mold.equalsIgnoreCase("explain")) {
                clazz = "com.mazhangjing.muninn.v2.entry.Note";
                Note note = (Note) forName(clazz).newInstance();
                if (date != null && !date.isEmpty())
                    note.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(date));
                note.setBody(map.get("text"));
                return note;
            } else if (mold.equalsIgnoreCase("blog")) {
                clazz = "com.mazhangjing.muninn.v2.entry.Link";
                Link link = (Link) forName(clazz).newInstance();
                if (date != null && !date.isEmpty())
                    link.setTime((new SimpleDateFormat("yyyy-MM-dd")).parse(date));
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

    //获取附加数据
    private List<PostScript> getPostScripts() {
        List<PostScript> result = new ArrayList<>();

        Elements selects = parse.getElementsByAttributeValue("type","related_note");
        selects.forEach(
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
                            getClass().getSimpleName(), post != null ? post.getClass().getSimpleName() : "Unknown PostScript"));
                    result.add(post);
                }
        );

        return result;
    }

    @Override
    public List<Chapter> doService(List<File> needParseHtmlFiles) {
        if (needParseHtmlFiles.isEmpty())
            logger.info(String.format("%s can't find file need to parse, skipping...",getClass().getSimpleName()));
        else
            logger.info(String.format("%s is Running...",getClass().getSimpleName()));
        return needParseHtmlFiles
                .stream()
                .filter(file -> {
                    boolean res = file.exists() && file.isFile() && file.canRead();
                    if (!res) logger.warn(String.format("%s Error: %s can't read/exist"
                            ,getClass().getSimpleName(),file));
                    return res;
                })
                .map(this::doParse).filter(Objects::nonNull).collect(Collectors.toList());
    }

    //对每个文件，进行读取、转换，调用 getXXX 方法获取信息，之后组装成 Chapter
    public Chapter doParse(File file) {
        Chapter chapter = null;
        try {
            //Read file info first
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String s; StringBuilder builder = new StringBuilder();
            while ((s = bufferedReader.readLine()) != null) { builder.append(s); }
            fileName = file.getName();
            parse = Jsoup.parse(builder.toString());
            bufferedReader.close(); reader.close();

            //covert content, metaInfo and postscripts
            List<Element> content = getContent(() -> "h1,h2,h3");
            List<String> metaInfo = getMetaInfo(() -> "p.card-text");
            List<PostScript> scripts = getPostScripts();
            chapter = new Chapter();

            //setting pojo carefully
            if (metaInfo.isEmpty() || metaInfo.size() != 4) {
                logger.warn("Find ipynb files and convert it，but can't get meta info from this file! " + file);
            } else {
                chapter.setTitle(metaInfo.get(0));
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
                chapter.setLastEdit(format.parse(metaInfo.get(1)));
                chapter.setVersion(metaInfo.get(2));
                chapter.setIntro(metaInfo.get(3));
            }
            chapter.setContent(
                    content.stream()
                           .map(Element::toString)
                           .collect(Collectors.joining("\n"))
            );
            chapter.setFileName(fileName);
            try {
                chapter.setNoteCount(Integer.valueOf(chapter.getVersion().split("\\.")[0]));
                chapter.setEditCount(Integer.valueOf(chapter.getVersion().split("\\.")[1]));
            } catch (Exception e) {
                logger.warn("can't parse noteCount and editCount from File! " + file + ", " + chapter.getTitle());
            }
            chapter.setPostscripts(new LinkedHashSet<>(scripts));

            logger.info(String.format("Finally Get POJO: %s",chapter));

        } catch (IOException | ParseException e) {
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            logger.warn(writer.toString());
        }
        return chapter;
    }
}
