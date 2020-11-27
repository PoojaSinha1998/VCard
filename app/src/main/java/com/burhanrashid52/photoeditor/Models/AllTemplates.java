package com.burhanrashid52.photoeditor.Models;

import android.graphics.Bitmap;

public class AllTemplates {
    byte[] bitmap;
    String id;

    public AllTemplates(byte[] bitmap, String id) {
        this.bitmap = bitmap;
        this.id = id;
    }

    public byte[] getBitmap() {
        return bitmap;
    }

    public void setBitmap(byte[] bitmap) {
        this.bitmap = bitmap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
