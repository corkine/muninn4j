package com.mazhangjing.muninn.v2.entry;

import java.util.Date;

//Explain
public class Note extends PostScript {
    //only body no head
    public Note(String id, Date time, String title, String body) {
        super(id, time, title, body);
    }

    public Note() {
    }
}
