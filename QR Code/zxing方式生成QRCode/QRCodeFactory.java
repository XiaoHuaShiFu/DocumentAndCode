package com.springboot.springboot.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取二维码的工厂
 * 通过zxing jar包
 * Created by xhsf on 2019/2/15.
 */
public class QRCodeFactory {

    private static Logger logger = LoggerFactory.getLogger(QRCodeFactory.class);

    /**
     * 获取二维码，以BufferedImage的形式返回
     * @param width 二维码宽度
     * @param height 二维码高度
     * @param margin 边框宽度
     * @param encoding 编码格式
     * @param contents 二维码内容
     * @param errorCorrectionLevel 纠错等级L(7%)、M(15%)、Q(25%)、H(30%)
     * @return {@link BufferedImage}
     */
    public static BufferedImage getQRCode(
            int width, int height, int margin, String encoding, String contents, ErrorCorrectionLevel errorCorrectionLevel) {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, encoding);
        hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);
        hints.put(EncodeHintType.MARGIN, margin);

        BitMatrix bitMatrix = null;
        try {
            bitMatrix = new MultiFormatWriter()
                    .encode(contents, BarcodeFormat.QR_CODE, width, height, hints);
        } catch (WriterException e) {
            logger.error("生成二维码出错", e);
        }
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    /**
     * 获取二维码信息，通过Result
     * @param encoding 编码格式
     * @param image 图片的{@link BufferedImage}流
     * @return {@link Result}
     */
    public static Result decodeQRCode(
            String encoding, BufferedImage image) {
        MultiFormatReader  multiFormatReader = new MultiFormatReader();
        Result result = null;
        try {
            BinaryBitmap binaryBitmap =
                    new BinaryBitmap(
                            new HybridBinarizer(
                                    new BufferedImageLuminanceSource(image)));
            Map hints = new HashMap();
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
            result = multiFormatReader.decode(binaryBitmap, hints);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

}
