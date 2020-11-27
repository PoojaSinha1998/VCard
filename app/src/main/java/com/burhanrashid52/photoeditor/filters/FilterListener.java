package com.burhanrashid52.photoeditor.filters;

import android.graphics.Bitmap;

import com.burhanrashid52.photoeditor.photoViewLib.PhotoFilter;



public interface FilterListener {
    void onFilterSelected(PhotoFilter photoFilter);
}