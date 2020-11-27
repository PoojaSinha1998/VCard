package com.vault.photoeditor.ApplicationClass;

import android.app.Application;

import com.vault.photoeditor.Models.AllTemplates;
import com.vault.photoeditor.Responses.SymbolsList;

import java.util.ArrayList;

public class ApplicationClass extends Application {

    private static ArrayList<AllTemplates> allTemplatesPro;
    private static ArrayList<AllTemplates> allTemplatesLan;
    private static ArrayList<SymbolsList> allSymbols;
    private static String orientattion ="";
    private static ArrayList<String> names;
    private static ArrayList<ArrayList<String>> grupedSym;

    public static String getOrientattion() {
        return orientattion;
    }

    public static void setOrientattion(String orientattion) {
        ApplicationClass.orientattion = orientattion;
    }



    public static void setAllTemplatePro(ArrayList<AllTemplates> allTemplates1) {
        ApplicationClass.allTemplatesPro = allTemplates1;
    }

    public static ArrayList<AllTemplates> getAllTemplates1() {
        return allTemplatesPro;
    }

    public static ArrayList<AllTemplates> getAllTemplatesLan() {
        return allTemplatesLan;
    }

    public static void setAllTemplatesLan(ArrayList<AllTemplates> allTemplatesLan) {
        ApplicationClass.allTemplatesLan = allTemplatesLan;
    }

    public static void setAllSymbols(ArrayList<SymbolsList> allSymbols) {
        ApplicationClass.allSymbols = allSymbols;
    }

    public static ArrayList<SymbolsList> getAllSymbols() {
        return allSymbols;
    }

    public static void setNames(ArrayList<String> names) {
        ApplicationClass.names = names;
    }

    public static ArrayList<String> getNames() {
        return names;
    }


    public static void setCategorizedSymbole(ArrayList<ArrayList<String>> grupedSym) {

        ApplicationClass.grupedSym = grupedSym;
    }

    public static ArrayList<ArrayList<String>> getGrupedSym() {
        return grupedSym;
    }
}
