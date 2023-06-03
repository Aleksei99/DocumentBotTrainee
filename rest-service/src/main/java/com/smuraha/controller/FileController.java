package com.smuraha.controller;

import com.smuraha.entity.AppDocument;
import com.smuraha.entity.AppPhoto;
import com.smuraha.entity.BinaryContent;
import com.smuraha.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping("/get-doc")
    public ResponseEntity<?> getDoc(@RequestParam("id") String id){
        //TODO для формирования badRequest добавить ControllerAdvice
        AppDocument document = fileService.getDocument(id);
        if(document == null){
            return ResponseEntity.badRequest().build();
        }
        BinaryContent binaryContent = document.getBinaryContent();
        FileSystemResource fileSystemResource = fileService.getFileSystemResource(binaryContent);
        if(fileSystemResource == null){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getMimeType()))
                .header("Content-disposition","attachment; filename="+document.getDocName())
                .body(fileSystemResource);
    }

    @GetMapping("/get-photo")
    public ResponseEntity<?> getPhoto(@RequestParam("id") String id){
        //TODO для формирования badRequest добавить ControllerAdvice
        AppPhoto photo = fileService.getPhoto(id);
        if(photo == null){
            return ResponseEntity.badRequest().build();
        }
        BinaryContent binaryContent = photo.getBinaryContent();
        FileSystemResource fileSystemResource = fileService.getFileSystemResource(binaryContent);
        if(fileSystemResource == null){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header("Content-disposition","attachment; ")
                .body(fileSystemResource);
    }

}
