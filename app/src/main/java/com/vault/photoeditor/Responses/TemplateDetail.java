package com.vault.photoeditor.Responses;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TemplateDetail {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("cardTemplateFileName")
    @Expose
    private String cardTemplateFileName;
    @SerializedName("cardTemplateOrientation")
    @Expose
    private String cardTemplateOrientation;
    @SerializedName("cardTemplateData")
    @Expose
    private String cardTemplateData;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCardTemplateFileName() {
        return cardTemplateFileName;
    }

    public void setCardTemplateFileName(String cardTemplateFileName) {
        this.cardTemplateFileName = cardTemplateFileName;
    }

    public String getCardTemplateOrientation() {
        return cardTemplateOrientation;
    }

    public void setCardTemplateOrientation(String cardTemplateOrientation) {
        this.cardTemplateOrientation = cardTemplateOrientation;
    }

    public String getCardTemplateData() {
        return cardTemplateData;
    }

    public void setCardTemplateData(String cardTemplateData) {
        this.cardTemplateData = cardTemplateData;
    }

}