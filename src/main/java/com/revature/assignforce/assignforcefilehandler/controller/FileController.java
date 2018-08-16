package com.revature.assignforce.assignforcefilehandler.controller;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.revature.assignforce.assignforcefilehandler.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
    public byte[] getFile(@RequestParam(value = "key") String key) throws IOException {
//        InputStream in = s3Object.getObjectContent();
//        byte[] buf = new byte[1024];
//        OutputStream out = new FileOutputStream(file);
//        while( (count = in.read(buf)) != -1)
//        {
//            if( Thread.interrupted() )
//            {
//                throw new InterruptedException();
//            }
//            out.write(buf, 0, count);
//        }
//        out.close();
//        in.close()
        S3Object data = fileService.get(key);
        File file = File.createTempFile("s3File","");
        int count;

        try (InputStream inputStream = data.getObjectContent(); OutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            while ((count = inputStream.read(buffer)) != -1) {
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                outputStream.write(buffer, 0, count);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return fileService.get(key);
    }

    @DeleteMapping("/")
    public boolean deleteFile(@RequestParam(value = "key") String key) {
        return fileService.delete(key);
    }
}
