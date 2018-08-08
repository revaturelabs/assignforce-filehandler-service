package com.revature.assignforce.assignforcefilehandlerservice.controller;

import com.revature.assignforce.assignforcefilehandlerservice.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
public class FileController {

    private FileService fileService;

    @Autowired
    public void FileController(FileService fileService) {
        this.fileService = fileService;
    }



    /**
     * returns key for the uploaded file
     * @return
     */
    @PostMapping("/")
    public String addFile(@RequestBody File file) {
        return fileService.save(file);
    }

    public File getFile(String key) {
        return fileService.get(key);
    }

    public void deleteFile(String key) {
        fileService.delete(key);
    }
}
