package com.revature.assignforce.assignforcefilehandler.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.revature.assignforce.assignforcefilehandler.model.FileData;
import com.revature.assignforce.assignforcefilehandler.model.Metadata;
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
     * @param data
     * @return
     */
    public String save(FileData data) {
        Metadata metadata = data.getMetadata();

        String key = metadata.getUploader() + "_" + data.getFile().getName();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.addUserMetadata("x-amz-meta-uploader", metadata.getUploader());

        PutObjectRequest object = new PutObjectRequest(bucket, key, data.getFile());
        object.setMetadata(objectMetadata);

        //put object into bucket and return key
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
