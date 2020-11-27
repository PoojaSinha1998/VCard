package com.burhanrashid52.photoeditor.Responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public  class SymbolsList {


        @SerializedName("categoriesList")
        @Expose
        private Object categoriesList;
        @SerializedName("groupedSymbolsDTOs")
        @Expose
        private ArrayList<GroupedSymbolsDTO> groupedSymbolsDTOs = null;

        public Object getCategoriesList() {
            return categoriesList;
        }

        public void setCategoriesList(Object categoriesList) {
            this.categoriesList = categoriesList;
        }

        public ArrayList<GroupedSymbolsDTO> getGroupedSymbolsDTOs() {
            return groupedSymbolsDTOs;
        }

        public void setGroupedSymbolsDTOs(ArrayList<GroupedSymbolsDTO> groupedSymbolsDTOs) {
            this.groupedSymbolsDTOs = groupedSymbolsDTOs;
        }

    }