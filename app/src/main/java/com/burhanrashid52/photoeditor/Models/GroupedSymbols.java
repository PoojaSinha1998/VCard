package com.burhanrashid52.photoeditor.Models;

import com.burhanrashid52.photoeditor.Responses.GroupedSymbolsDTO;

import java.util.ArrayList;

public class GroupedSymbols {

    private String id;

    private String symbolName;
    private String category;
    private String symbolData;
    private ArrayList<GroupedSymbolsDTO> groupedSymbolsDTOs;

    public GroupedSymbols(ArrayList<GroupedSymbolsDTO> groupedSymbolsDTOs) {
        this.groupedSymbolsDTOs = groupedSymbolsDTOs;
    }


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