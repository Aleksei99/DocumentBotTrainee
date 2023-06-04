package com.smuraha.service.impl;

import com.smuraha.dto.MailParams;
import com.smuraha.service.MailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailSenderServiceImpl implements MailSenderService {

    @Value("${spring.mail.username}")
    private String emailFrom;
    @Value("${service.activation.uri}")
    private String activationServiceUri;

    private final JavaMailSender javaMailSender;

    @Override
    public void send(MailParams mailParams) {
        String subject = "Активация учётки";
        String body = getActivationMailBody(mailParams.getId());
        String emailTo = mailParams.getEmailTo();

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(emailFrom);
        simpleMailMessage.setTo(emailTo);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);

        javaMailSender.send(simpleMailMessage);
    }

    private String getActivationMailBody(String id) {
        String msg = """
                Для завершения регистрации перейдите по ссылке
                {link}
                """;
        return msg.replace("{link}",activationServiceUri).replace("{id}",id);
    }
}
