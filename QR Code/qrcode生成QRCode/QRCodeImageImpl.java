package com.springboot.springboot.util;

import jp.sourceforge.qrcode.data.QRCodeImage;

import java.awt.image.BufferedImage;

/**
 * 实现QRCodeImage接口
 * Created by xhsf on 2019/2/16.
 */
public class QRCodeImageImpl implements QRCodeImage {
    BufferedImage bufferedImage;
    public QRCodeImageImpl(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }
    @Override
    public int getWidth() {
        return bufferedImage.getWidth();
    }
    @Override
    public int getHeight() {
        return bufferedImage.getHeight();
    }
    @Override
    public int getPixel(int i, int i1) {
        return bufferedImage.getRGB(i, i1);
    }
}

