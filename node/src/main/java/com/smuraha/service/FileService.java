package com.smuraha.service;

import com.smuraha.entity.AppDocument;
import com.smuraha.entity.AppPhoto;
import com.smuraha.service.enums.LinkType;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);
    String generateLink(Long docId, LinkType linkType);
}
