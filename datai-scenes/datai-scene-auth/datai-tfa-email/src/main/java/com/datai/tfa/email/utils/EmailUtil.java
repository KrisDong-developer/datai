package com.datai.tfa.email.utils;

import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.datai.common.exception.ServiceException;
import com.datai.common.utils.StringUtils;
import com.datai.common.utils.spring.SpringUtils;

public class EmailUtil {
    public static void sendMessage(String email, String title, String message) {
        if (StringUtils.isEmpty(email)) {
            throw new ServiceException("邮箱不能为空");
        }
        MailProperties mailProperties = SpringUtils.getBean(MailProperties.class);
        JavaMailSenderImpl mailSender = SpringUtils.getBean(JavaMailSenderImpl.class);
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject(title);
        simpleMailMessage.setText(message);
        simpleMailMessage.setFrom(mailProperties.getUsername());
        simpleMailMessage.setTo(email);
        mailSender.send(simpleMailMessage);
    }
}
