package com.springboot.springboot.util;

/**
 * Created by lenovo on 2019/2/14.
 */

/**
 * 验证码pojo类
 */
public class VerificationCode {
    private String imagePath; //验证图片路径
    private String code; //验证码
    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
}
