package com.revature.assignforce.assignforcefilehandlerservice.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import com.amazonaws.services.s3.AmazonS3Client;

@Service
public class FileService {

    private AmazonS3Client client;
    private final String BUCKET = "test-bucket";

    public AmazonS3Client getClient() {
        AwsClientBuilder.EndpointConfiguration endpoint = new AwsClientBuilder.EndpointConfiguration("http://localhost:8765", "us-west-2");
        client = (AmazonS3Client) AmazonS3ClientBuilder
                .standard()
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(endpoint)
                .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
                .build();
        return client;
    }

    public void setClient(AmazonS3Client client) {
        this.client = client;
    }

    public String save(File file, String key) {
        AmazonS3Client client = getClient();
        //put object into bucket and return key
        client.createBucket(BUCKET);
        client.putObject(BUCKET,"test",file);
        return key;
    }

    public S3Object get(String key) {
        AmazonS3Client client = getClient();
        try {
            return client.getObject(BUCKET, key);
        } catch(Exception e) {
            return null;
        }
    }

    public int delete(String key) {
        AmazonS3Client client = getClient();
        try {
            client.deleteObject(BUCKET, key);
            return 1;
        } catch(Exception e) {
            return 0;
        }
    }
}
