package com.goldentech.model;

import java.io.Serializable;

public class NavViewModel implements Serializable {
    private int id;
    private int image;
    private String textName;
    private int ViewType;
    private String textHeaderName;
    public static final int NoHeader = 1;
    public static final int Header = 2;

    private String count;

    public NavViewModel(String textHeaderName, int ViewType) {
        this.textHeaderName = textHeaderName;
        this.ViewType = ViewType;
    }


    public NavViewModel(int id, int image, String textName, int ViewType) {
        this.id = id;
        this.image = image;
        this.textName = textName;
        this.ViewType = ViewType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTextName() {
        return textName;
    }

    public void setTextName(String textName) {
        this.textName = textName;
    }

    public int getViewType() {
        return ViewType;
    }

    public void setViewType(int viewType) {
        ViewType = viewType;
    }

    public String getTextHeaderName() {
        return textHeaderName;
    }

    public void setTextHeaderName(String textHeaderName) {
        this.textHeaderName = textHeaderName;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
