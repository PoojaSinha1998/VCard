package com.burhanrashid52.photoeditor.Responses;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SavedVisiting {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("designedCardFileName")
    @Expose
    private String designedCardFileName;
    @SerializedName("designedCardOrientation")
    @Expose
    private String designedCardOrientation;
    @SerializedName("designedCardData")
    @Expose
    private String designedCardData;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesignedCardFileName() {
        return designedCardFileName;
    }

    public void setDesignedCardFileName(String designedCardFileName) {
        this.designedCardFileName = designedCardFileName;
    }

    public String getDesignedCardOrientation() {
        return designedCardOrientation;
    }

    public void setDesignedCardOrientation(String designedCardOrientation) {
        this.designedCardOrientation = designedCardOrientation;
    }

    public String getDesignedCardData() {
        return designedCardData;
    }

    public void setDesignedCardData(String designedCardData) {
        this.designedCardData = designedCardData;
    }

}