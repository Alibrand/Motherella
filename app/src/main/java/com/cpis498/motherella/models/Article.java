package com.cpis498.motherella.models;

import java.util.Map;

public class Article {
    String id;
    String title;
    String link;


    public Article(Map<String,Object> map) {
        this.id=(String) map.get("id");
        this.title=(String) map.get("title");
        this.link=(String) map.get("link");
    }

    public Article() {
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
