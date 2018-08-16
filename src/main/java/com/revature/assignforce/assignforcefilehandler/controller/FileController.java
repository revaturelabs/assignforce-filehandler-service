package com.revature.assignforce.assignforcefilehandler.controller;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.revature.assignforce.assignforcefilehandler.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;

@CrossOrigin
@RestController
public class FileController {

    private FileService fileService;

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * Accepts a file and metadata (?) from the request body.
     * TODO: switch from temporary key to metadata.
     * @return
     */
    @PostMapping("/")
    public String addFile(@RequestParam("file") MultipartFile file, @RequestParam("category") String category, @RequestParam("trainer") int trainer) {
        try {
            return fileService.save(file, category, trainer);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping(value = "/")
    public ResponseEntity<byte[]> getFile(@RequestParam(value = "key") String key) throws IOException {
        byte[] media = fileService.get(key);

        return new ResponseEntity<>(media, HttpStatus.OK);
    }

    @DeleteMapping("/")
    public boolean deleteFile(@RequestParam(value = "key") String key) {
        return fileService.delete(key);
    }
}
