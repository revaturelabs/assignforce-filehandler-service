package com.revature.assignforce.assignforcefilehandlerservice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
public class FileController {



    /**
     * returns key for the uploaded file
     * @return
     */
    @PostMapping("/")
    public String addFile(@RequestBody File file) {
        return "";
    }
}
