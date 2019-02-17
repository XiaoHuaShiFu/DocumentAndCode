package com.springjiemi.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * 邮件操作的类
 * Created by xhsf on 2019/2/18.
 */
public class EmailUtil {

    /**
     * 发送邮件
     * @param to 收件人地址
     * @param code 激活码
     * @throws MessagingException
     */
    public static void sendEmail(String to, String code) throws MessagingException {
        //1.创建连接对象，连接到邮件服务器
        Properties properties = new Properties(); //可以设置主机名，发送的服务器地址等,如properties.setProperty("host",value);
        Authenticator authenticator = new Authenticator() { //登录到邮箱服务器的认证属性类
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("service@store.com", "123");
            }
        };
        Session session = Session.getInstance(properties, authenticator); //连接对象
        //2.创建邮件对象
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("service@store.com")); //设置发件人
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to)); //设置收件人
        message.setSubject("激活邮件fromXHSF"); //设置邮件主题
        message.setContent("<h1>来自xhsf的激活邮件信息</h1>", "text/html;charset=UTF-8"); //设置正文
        //3.发送邮件
        Transport.send(message);
    }

}
