package com.goldentech.model;

import java.io.Serializable;

public class Language implements Serializable {

    private String id;
    private String language;

    public Language(String id, String language) {
        this.id = id;
        this.language = language;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
