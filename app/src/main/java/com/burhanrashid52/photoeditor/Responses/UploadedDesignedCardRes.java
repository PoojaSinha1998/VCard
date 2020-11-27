package com.burhanrashid52.photoeditor.Responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UploadedDesignedCardRes {

    @SerializedName("responseType")
    @Expose
    private String responseType;
    @SerializedName("SymbolsList")
    @Expose
    private ArrayList<SavedVisiting> symbolsList = null;
    @SerializedName("responseMessage")
    @Expose
    private String responseMessage;
    @SerializedName("responseCode")
    @Expose
    private String responseCode;

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public ArrayList<SavedVisiting> getSymbolsList() {
        return symbolsList;
    }

    public void setSymbolsList(ArrayList<SavedVisiting> symbolsList) {
        this.symbolsList = symbolsList;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

}