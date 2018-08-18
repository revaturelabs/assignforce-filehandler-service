package com.revature.assignforce.assignforcefilehandler.controller;

import com.revature.assignforce.assignforcefilehandler.service.FileService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;


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
    public ResponseEntity<byte[]> getFile(@RequestParam(value = "key") String key) {
        try(InputStream inputStream = fileService.get(key).getObjectContent()){
            byte[] media = IOUtils.toByteArray(inputStream);

            return new ResponseEntity<>(media, HttpStatus.OK);
        } catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/")
    public boolean deleteFile(@RequestParam(value = "key") String key) {
        return fileService.delete(key);
    }
}
