package com.springboot.springboot.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

/**
 * 生成验证码类{@link VerificationCode}的工厂
 * 可以在spring中设置为bean，供生成验证码使用
 * Created by xhsf on 2019/2/14.
 */
public class VerificationCodeFactory {

    private String charset = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789"; //图片字符集
    private Color backgroundColor = new Color(200, 150, 255); //图片背景颜色
    private String filePath = null; //图片暂存路径

    /**
     * 必须指定暂存文件路径
     * @param filePath
     */
    public VerificationCodeFactory(String filePath){
        this.filePath = filePath;
    }

    /**
     * 获取验证码
     * @return VerificationCode
     * @throws IOException (如果文件路径错误)
     */
    public VerificationCode getVerificationCode() throws IOException {
        //填充背景
        BufferedImage bufferedImage = new BufferedImage(68, 22, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.getGraphics();
        Color color = this.backgroundColor;
        graphics.setColor(color);
        graphics.fillRect(0, 0, 68, 22);
        //生成随机数
        Random random = new Random();
        int len = charset.length();
        StringBuilder codeBuilder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int index = random.nextInt(len);
            graphics.setColor(new Color(
                    random.nextInt(88),
                    random.nextInt(188),
                    random.nextInt(255)));
            graphics.drawString(charset.substring(index, index + 1), (i * 15) + 3, 18);
            codeBuilder.append(charset.substring(index, index + 1));
        }
        //把图片添加到文件
        String vImagePath = filePath + UUID.randomUUID().toString() + ".jpg";
        ImageIO.write(bufferedImage, "JPG", new File(vImagePath));
        //装配验证码类
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setImagePath(vImagePath);
        verificationCode.setCode(codeBuilder.toString());
        return verificationCode;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

}
