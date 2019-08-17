package com.mazhangjing.muninn.v1.Entry;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

public abstract class PostScript {
    private String id;
    private Date time;
	private String title;
	private String body;

    @Override
    public String toString() {
        return "PostScript{" +
                "id='" + id + '\'' +
                ", time=" + time +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostScript that = (PostScript) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(time, that.time) &&
                Objects.equals(title, that.title) &&
                Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, time, title, body);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public PostScript(String id, Date time, String title, String body) {
        this.id = id;
        this.time = time;
        this.title = title;
        this.body = body;
    }

    public PostScript() {
    }
}
