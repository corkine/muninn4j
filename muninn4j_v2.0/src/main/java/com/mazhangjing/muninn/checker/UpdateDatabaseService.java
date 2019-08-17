package com.mazhangjing.muninn.checker;

import com.mazhangjing.muninn.v1.Entry.Chapter;
import com.mazhangjing.muninn.v1.Entry.Volume;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.List;

public class UpdateDatabaseService {

    @Test public void test() {
        //整个Checker 的流程：从全局配置文件加载元信息，构造包含元信息的课程对象（对象现在不包含内部的章节）；
        //之后对新修改的章节进行迁移、文件格式转换，之后从这些HTML文件中提取这些新修改章节的信息，根据元数据，查找到此章节所属
        //的课程，以及确定此章节的 id。并且将其添加到课程对象中。
        //现在我们获得的 Volumes 中的对象，将会是所有的 课程对象，但是，对象仅包含更新的信息，也就是说，只有更新的章节在此处
        //才会显示，所以说，我们不能够直接保存课程对象，否则的话，所有的章节信息都会被覆盖。
        Configure configure = new Yaml().loadAs(getClass().getClassLoader().getResourceAsStream("meta.yml"), Configure.class);
        List<File> files = JupyterNoteBookMoveService.getService(true,configure).doService();
        List<Chapter> chapters = new HtmlParseService().doService(files);
        List<Volume> volumes = new MetaInfoSetService().doService(chapters, configure);
        System.out.println("volumes = " + volumes);
        System.out.println("volumes.get(0).getChapters() = " + volumes.get(0).getChapters());
    }
}
