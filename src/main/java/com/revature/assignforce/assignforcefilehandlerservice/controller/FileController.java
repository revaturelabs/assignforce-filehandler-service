package com.revature.assignforce.assignforcefilehandlerservice.controller;

import com.amazonaws.services.s3.model.S3Object;
import com.revature.assignforce.assignforcefilehandlerservice.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
public class FileController {
    @Autowired
    private FileService fileService;

    /**
     * returns key for the uploaded file
     * @return
     */
    @PostMapping("/")
    public String addFile(@RequestBody File file, @RequestBody String key) {
        return fileService.save(file, key);
    }

    @GetMapping("/")
    public S3Object getFile(@RequestParam(value="key") String key) {
        return fileService.get(key);
    }

    @DeleteMapping("/")
    public int deleteFile(@RequestParam(value="key") String key) {
        return fileService.delete(key);
    }
}
