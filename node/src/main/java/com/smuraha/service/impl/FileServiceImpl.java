package com.smuraha.service.impl;

import com.smuraha.dao.AppDocumentDao;
import com.smuraha.dao.BinaryContentDao;
import com.smuraha.entity.AppDocument;
import com.smuraha.entity.BinaryContent;
import com.smuraha.exceptions.UploadFileException;
import com.smuraha.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

@Log4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    @Value("${token}")
    private String token;
    @Value("${service.file.info_uri}")
    private String infoUri;
    @Value("${service.file.storage_uri}")
    private String storageUri;

    private final AppDocumentDao documentDao;
    private final BinaryContentDao binaryContentDao;

    @Override
    public AppDocument processDoc(Message telegramMessage) {
        Document document = telegramMessage.getDocument();
        String fileId = document.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.getBody()));
            String filePath = String.valueOf(jsonObject.getJSONObject("result")
                    .getString("file_path"));
            byte[] fileInByte = downloadFile(filePath);
            BinaryContent transientBinaryContent = BinaryContent.builder()
                    .fileAsArrayOfBytes(fileInByte)
                    .build();
            BinaryContent persistentBinaryContent = binaryContentDao.save(transientBinaryContent);
            AppDocument transientAppDoc = buildTransientAppDoc(document,persistentBinaryContent);
            return documentDao.save(transientAppDoc);
        }else {
            throw new UploadFileException("Bad response from telegram service: "+ response);
        }
    }

    private AppDocument buildTransientAppDoc(Document document, BinaryContent persistentBinaryContent) {
        return AppDocument.builder()
                .telegramFileId(document.getFileId())
                .docName(document.getFileName())
                .fileSize(document.getFileSize())
                .binaryContent(persistentBinaryContent)
                .mimeType(document.getMimeType())
                .build();
    }

    private byte[] downloadFile(String filePath) {
        String fullUri = storageUri.replace("{token}", token).replace("{filePath}", filePath);
        URL urlObject;
        try {
            urlObject = new URL(fullUri);
        } catch (MalformedURLException e) {
            throw new UploadFileException(e);
        }
        try (InputStream inputStream = urlObject.openStream()){
            return inputStream.readAllBytes();
        }catch (IOException e){
            throw new UploadFileException(urlObject.toExternalForm(),e);
        }

    }

    private ResponseEntity<String> getFilePath(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);

        return restTemplate.exchange(
                infoUri,
                HttpMethod.GET,
                httpEntity,
                String.class,
                token, fileId
        );

    }
}
