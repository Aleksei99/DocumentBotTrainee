package com.smuraha.service;

import com.smuraha.dto.MailParams;

public interface MailSenderService {
    void send(MailParams mailParams);
}
