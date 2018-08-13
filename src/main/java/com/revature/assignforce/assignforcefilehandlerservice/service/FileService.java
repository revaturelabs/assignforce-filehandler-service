package com.revature.assignforce.assignforcefilehandlerservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

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
     * TODO: switch from accepting key to accepting metadata (object? string?) and generating key.
     * @param file
     * @param key
     * @return
     */
    public String save(File file, String key) {
        //put object into bucket and return key
        client.createBucket(bucket);
        client.putObject(bucket, "test", file);
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
