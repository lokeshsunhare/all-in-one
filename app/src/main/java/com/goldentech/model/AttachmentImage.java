package com.goldentech.model;

import android.net.Uri;

import java.io.Serializable;

public class AttachmentImage implements Serializable {

    private String image_name;
    private Uri image_Uri;

    public AttachmentImage(String image_name, Uri image_Uri) {
        this.image_name = image_name;
        this.image_Uri = image_Uri;
    }

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public Uri getImage_Uri() {
        return image_Uri;
    }

    public void setImage_Uri(Uri image_Uri) {
        this.image_Uri = image_Uri;
    }
}
