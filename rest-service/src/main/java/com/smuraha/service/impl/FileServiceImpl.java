package com.smuraha.service.impl;

import com.smuraha.dao.AppDocumentDao;
import com.smuraha.dao.AppPhotoDao;
import com.smuraha.entity.AppDocument;
import com.smuraha.entity.AppPhoto;
import com.smuraha.entity.BinaryContent;
import com.smuraha.service.FileService;
import com.smuraha.utils.CryptoTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Log4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final AppDocumentDao documentDao;
    private final AppPhotoDao appPhotoDao;
    private final CryptoTool cryptoTool;

    @Override
    public AppDocument getDocument(String hash) {
        Long id = cryptoTool.idOf(hash);
        if(id==null){
            return null;
        }
        return documentDao.findById(id).orElse(null);
    }

    @Override
    public AppPhoto getPhoto(String hash) {
        Long id = cryptoTool.idOf(hash);
        if(id==null){
            return null;
        }
        return appPhotoDao.findById(id).orElse(null);
    }

    @Override
    public FileSystemResource getFileSystemResource(BinaryContent binaryContent) {
        try {
            File tempFile = File.createTempFile("tempFile", ".bin");
            tempFile.deleteOnExit();
            FileUtils.writeByteArrayToFile(tempFile,binaryContent.getFileAsArrayOfBytes());
            return new FileSystemResource(tempFile);
        } catch (IOException e) {
            log.error(e);
            return null;
        }
    }
}
