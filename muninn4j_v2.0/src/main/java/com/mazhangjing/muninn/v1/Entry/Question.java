package com.mazhangjing.muninn.v1.Entry;

import java.time.LocalDateTime;
import java.util.Date;

public class Question extends PostScript {

    public Question(String id, Date time, String title, String body) {
        super(id, time, title, body);
    }

    public Question() {
    }
}
