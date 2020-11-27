package com.vault.photoeditor.Responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GroupedSymbolsDTO {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("symbolName")
    @Expose
    private String symbolName;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("symbolData")
    @Expose
    private String symbolData;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSymbolName() {
        return symbolName;
    }

    public void setSymbolName(String symbolName) {
        this.symbolName = symbolName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSymbolData() {
        return symbolData;
    }

    public void setSymbolData(String symbolData) {
        this.symbolData = symbolData;
    }

}