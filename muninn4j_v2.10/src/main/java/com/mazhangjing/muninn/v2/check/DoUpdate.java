package com.mazhangjing.muninn.v2.check;

import com.mazhangjing.muninn.v2.dao.JdbcVolumeDao;
import com.mazhangjing.muninn.v2.dao.VolumeDao;
import org.yaml.snakeyaml.Yaml;

public class DoUpdate {

    public static void main(String[] args) {

        Configure configure =
                new Yaml().loadAs(DoUpdate.class.getClassLoader().getResourceAsStream("meta_server.yml"), Configure.class);
        JupyterNoteBookMoveService moveService = JupyterNoteBookMoveService.getService(true,configure);
        ParseService parseService = new HtmlParseService();
        BeanService beanService = new MetaInfoSetService();
        VolumeDao dao = new JdbcVolumeDao();
        new UpdateDatabaseService().doService(configure, moveService, parseService, beanService, dao);
    }

}
