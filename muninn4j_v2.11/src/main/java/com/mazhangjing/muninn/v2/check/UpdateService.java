package com.mazhangjing.muninn.v2.check;

import com.mazhangjing.muninn.v2.dao.VolumeDao;

public interface UpdateService {
    void doService(Configure configure,
                   TransService moveService,
                   ParseService parseService,
                   BeanService beanService,
                   VolumeDao dao);
}
