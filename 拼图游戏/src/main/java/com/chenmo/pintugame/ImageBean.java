package com.chenmo.pintugame;

import android.graphics.Bitmap;

/**
 * 作者：沉默
 * 日期：2017/3/2
 * QQ:823925783
 */

public class ImageBean {

    private Bitmap bitmap;
    private int  index;

    public ImageBean() {
    }

    public ImageBean(Bitmap bitmap, int index) {
        this.bitmap = bitmap;
        this.index = index;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
