package com.revature.assignforce.assignforcefilehandler.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileService {

    private AmazonS3 client;

    @Value("${service.s3.bucket}")
    private String bucket;

    @Autowired
    public void setClient(AmazonS3 client) {
        this.client = client;
    }

    /**
     * Creates key from file metadata, uploads file to S3 with generated key.
     * If successful upload, return key.
     * @param file
     * @param category
     * @param trainer_id
     * @return
     */
    public String save(MultipartFile file, String category, int trainer_id) throws IOException {

        String key = category + "/" + trainer_id + "-" + file.getOriginalFilename();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.addUserMetadata("x-amz-meta-trainer", "id_" + trainer_id);

        PutObjectRequest object = new PutObjectRequest(bucket, key, file.getInputStream(), objectMetadata);
        object.setMetadata(objectMetadata);

        // put object into bucket and return key
        client.createBucket(bucket);
        client.putObject(object);

        return key;
    }

    /**
     *
     * @param key
     * @return
     */
    public S3Object get(String key) {
        try {
            return client.getObject(bucket, key);
        } catch(Exception e) {
            return null;
        }
    }

    public boolean delete(String key) {
        try {
            client.deleteObject(bucket, key);
            return true;
        } catch(Exception e) {
            return false;
        }
    }
}
