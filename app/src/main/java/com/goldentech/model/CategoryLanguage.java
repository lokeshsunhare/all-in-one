package com.goldentech.model;

import java.io.Serializable;

public class CategoryLanguage implements Serializable {

    private String languageID;
    private String languageName;
    private String categoryName;
    private boolean isChecked;
    private int checkedCount = 1;

    public CategoryLanguage(String languageID, String languageName, String categoryName,
                            boolean isChecked) {
        this.languageID = languageID;
        this.languageName = languageName;
        this.categoryName = categoryName;
        this.isChecked = isChecked;
    }

    public String getLanguageID() {
        return languageID;
    }

    public void setLanguageID(String languageID) {
        this.languageID = languageID;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getCheckedCount() {
        return checkedCount;
    }

    public void setCheckedCount(int checkedCount) {
        this.checkedCount = checkedCount;
    }
}
