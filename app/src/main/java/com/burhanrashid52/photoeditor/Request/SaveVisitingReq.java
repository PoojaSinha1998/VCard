package com.burhanrashid52.photoeditor.Request;

public class SaveVisitingReq {

    String designedCardFileName;
    String designedCardData;
    String designedCardOrientation;

    public String getDesignedCardOrientation() {
        return designedCardOrientation;
    }

    public void setDesignedCardOrientation(String designedCardOrientation) {
        this.designedCardOrientation = designedCardOrientation;
    }

    public String getDesignedCardFileName() {
        return designedCardFileName;
    }

    public void setDesignedCardFileName(String designedCardFileName) {
        this.designedCardFileName = designedCardFileName;
    }

    public String getDesignedCardData() {
        return designedCardData;
    }

    public void setDesignedCardData(String designedCardData) {
        this.designedCardData = designedCardData;
    }
}
