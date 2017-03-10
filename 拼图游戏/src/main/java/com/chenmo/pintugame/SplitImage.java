package com.chenmo.pintugame;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：沉默
 * 日期：2017/3/2
 * QQ:823925783
 */

public class SplitImage {
    /**
     * @param bitmap 传入bitmap
     * @param piece  把图片分块piece*piece
     * @return List<ImageBean>
     */
    public static List<ImageBean> spliteimage(Bitmap bitmap, int piece) {
        List<ImageBean> split = new ArrayList<>();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int pieceWidth = Math.min(width, height) / piece;
        for (int i = 0; i < piece; i++) {
            for (int j = 0; j < piece; j++) {
                ImageBean imageBean = new ImageBean();
                imageBean.setIndex(j + i * piece);
                int x = j * pieceWidth;
                int y = i * pieceWidth;
                imageBean.setBitmap(Bitmap.createBitmap(bitmap, x, y, pieceWidth, pieceWidth));
                split.add(imageBean);
            }
        }
        return split;
    }
}
