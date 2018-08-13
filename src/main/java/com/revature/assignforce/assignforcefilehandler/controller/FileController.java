package com.revature.assignforce.assignforcefilehandler.controller;

import com.amazonaws.services.s3.model.S3Object;
import com.revature.assignforce.assignforcefilehandler.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;

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
    public String addFile(@RequestBody File file, @RequestBody String key) {
        return fileService.save(file, key);
    }

    @GetMapping("/")
    public S3Object getFile(@RequestParam(value="key") String key) {
        return fileService.get(key);
    }

    @DeleteMapping("/")
    public boolean deleteFile(@RequestParam(value="key") String key) {
        return fileService.delete(key);
    }
}
