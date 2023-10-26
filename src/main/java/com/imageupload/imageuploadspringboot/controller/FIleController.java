package com.imageupload.imageuploadspringboot.controller;

import com.imageupload.imageuploadspringboot.model.LoadFile;
import com.imageupload.imageuploadspringboot.service.fileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin("*")
@RequestMapping("file")
@Slf4j
public class FIleController {

    @Autowired
    private fileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestBody MultipartFile file) throws IOException {
        log.info("Upload image method is invoked");
       return new ResponseEntity<>(fileService.addFile(file), HttpStatus.OK);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<ByteArrayResource> download(@PathVariable String id) throws IOException {
        log.info("Download image method is invoked");
        LoadFile loadFile = fileService.downloadFile(id);
        if(loadFile != null && loadFile.getFileType() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(loadFile.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + loadFile.getFileName() + "\"")
                    .body(new ByteArrayResource(loadFile.getFile()));
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/get-all-file")
    public ResponseEntity<?> displayAllId() throws IOException {
        log.info("Dsiply All Stored Id Method Invoked");
        return new ResponseEntity<>(fileService.getAllFileDetails(), HttpStatus.OK);
    }

}
