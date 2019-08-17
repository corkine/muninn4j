package com.mazhangjing.muninn.v2.entry;

import java.util.*;

public class Chapter {
    private String id;
	private String title;
	private String version;
	private Integer noteCount;
	private Integer editCount;
	private String intro;
	private Date lastEdit;
	private Volume volume;
	private String content;
	private String fileName;
	private int orders;

    public int getOrders() { return orders; }

    public void setOrders(int order) { this.orders = order; }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private Set<PostScript> postscripts = new LinkedHashSet<>();

    @Override
    public String toString() {
        return "Chapter{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", version='" + version + '\'' +
                ", noteCount=" + noteCount +
                ", editCount=" + editCount +
                ", lastEdit=" + lastEdit +
                ", volume=" + volume +
                ", order=" + orders +
                ", fileName='" + fileName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chapter chapter = (Chapter) o;
        return Objects.equals(id, chapter.id) &&
                Objects.equals(title, chapter.title) &&
                Objects.equals(version, chapter.version) &&
                Objects.equals(noteCount, chapter.noteCount) &&
                Objects.equals(editCount, chapter.editCount) &&
                Objects.equals(intro, chapter.intro) &&
                Objects.equals(lastEdit, chapter.lastEdit) &&
                Objects.equals(volume, chapter.volume) &&
                Objects.equals(content, chapter.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, version, noteCount, editCount, intro, lastEdit, volume, content);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Chapter(String id, String title, String version, Integer noteCount, Integer editCount, String intro, Date lastEdit, Volume volume, String content) {
        this.id = id;
        this.title = title;
        this.version = version;
        this.noteCount = noteCount;
        this.editCount = editCount;
        this.intro = intro;
        this.lastEdit = lastEdit;
        this.volume = volume;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getNoteCount() {
        return noteCount;
    }

    public void setNoteCount(Integer noteCount) {
        this.noteCount = noteCount;
    }

    public Integer getEditCount() {
        return editCount;
    }

    public void setEditCount(Integer editCount) {
        this.editCount = editCount;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public Date getLastEdit() {
        return lastEdit;
    }

    public void setLastEdit(Date lastEdit) {
        this.lastEdit = lastEdit;
    }

    public Volume getVolume() {
        return volume;
    }

    public void setVolume(Volume volume) {
        this.volume = volume;
    }

    public Set<PostScript> getPostscripts() {
        return postscripts;
    }

    public void setPostscripts(Set<PostScript> postscripts) {
        this.postscripts = postscripts;
    }

    public Chapter(String id, String title, String version, Integer noteCount, Integer editCount, String intro, Date lastEdit) {
        this.id = id;
        this.title = title;
        this.version = version;
        this.noteCount = noteCount;
        this.editCount = editCount;
        this.intro = intro;
        this.lastEdit = lastEdit;
    }

    public Chapter(String id, String title, String version, Integer noteCount, Integer editCount, String intro, Date lastEdit, Volume volume) {
        this.id = id;
        this.title = title;
        this.version = version;
        this.noteCount = noteCount;
        this.editCount = editCount;
        this.intro = intro;
        this.lastEdit = lastEdit;
        this.volume = volume;
    }

    public Chapter() {
    }
}
