package com.mazhangjing.muninn.v2.entry;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class Volume {

    private String id;
    private String title;
	private String intro;
	private String category;
	private String link;
	private Set<Chapter> chapters = new HashSet<>();

    @Override
    public String toString() {
        return "Volume{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", intro='" + intro + '\'' +
                ", category='" + category + '\'' +
                ", link='" + link + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Volume volume = (Volume) o;
        return Objects.equals(id, volume.id) &&
                Objects.equals(title, volume.title) &&
                Objects.equals(intro, volume.intro) &&
                Objects.equals(category, volume.category) &&
                Objects.equals(link, volume.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, intro, category, link);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Volume(String id, String title, String intro, String category, String link) {
        this.id = id;
        this.title = title;
        this.intro = intro;
        this.category = category;
        this.link = link;
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

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Set<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(Set<Chapter> chapters) {
        this.chapters = chapters;
    }

    public Volume(String id, String title, String intro, String link) {
        this.id = id;
        this.title = title;
        this.intro = intro;
        this.link = link;
    }

    public Volume() {
    }
}
