package com.burhanrashid52.photoeditor.MyViewModel;

import android.graphics.drawable.Drawable;

import androidx.lifecycle.ViewModel;

public class BackgroundViewModel extends ViewModel {
    private  Drawable selected ;

    public void select(Drawable item) {
        selected=(item);
    }

    public Drawable getSelected() {
        return selected;
    }
}
