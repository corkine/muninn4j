package com.mazhangjing.muninn.v1.Entry;

import java.time.LocalDateTime;
import java.util.Date;

public class Quote extends PostScript {
    //no body, only title

    private String footer;

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public Quote(String id, Date time, String title, String body) {
        super(id, time, title, body);
    }

    public Quote() {
    }
}
