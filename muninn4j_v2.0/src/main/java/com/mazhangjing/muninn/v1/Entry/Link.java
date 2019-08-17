package com.mazhangjing.muninn.v1.Entry;

import java.time.LocalDateTime;
import java.util.Date;
//Blog
public class Link extends PostScript {

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Link(String id, Date time, String title, String body) {
        super(id, time, title, body);
    }

    public Link() {
    }
}
