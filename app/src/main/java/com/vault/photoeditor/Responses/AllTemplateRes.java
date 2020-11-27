package com.vault.photoeditor.Responses;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllTemplateRes {

    @SerializedName("responseType")
    @Expose
    private String responseType;
    @SerializedName("TemplateDetails")
    @Expose
    private ArrayList<TemplateDetail> templateDetails = null;
    @SerializedName("responseCode")
    @Expose
    private String responseCode;

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public ArrayList<TemplateDetail> getTemplateDetails() {
        return templateDetails;
    }

    public void setTemplateDetails(ArrayList<TemplateDetail> templateDetails) {
        this.templateDetails = templateDetails;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

}
