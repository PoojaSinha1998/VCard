package com.vault.photoeditor.Responses;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllSymbolsRes {

        @SerializedName("responseType")
        @Expose
        private String responseType;
        @SerializedName("SymbolsList")
        @Expose
        private ArrayList<SymbolsList> symbolsList = null;
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

        public ArrayList<SymbolsList> getSymbolsList() {
            return symbolsList;
        }

        public void setSymbolsList(ArrayList<SymbolsList> symbolsList) {
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

