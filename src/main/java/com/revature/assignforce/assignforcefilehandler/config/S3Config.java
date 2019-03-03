package com.revature.assignforce.assignforcefilehandler.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TODO: integrate remote S3 server with sections commented out below.
 */
@Configuration
public class S3Config {
    @Value("${service.aws.access_key_id}")
    private String accessId;

    @Value("${service.aws.secret_access_key}")
    private String accessKey;

    @Value("${service.s3.url}")
    private String url;

    @Value("${service.s3.region}")
    private String region;

    @Value("${service.s3.bucket}")
    private String bucket;

    @Bean
    public AmazonS3 client() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessId, accessKey);
        AwsClientBuilder.EndpointConfiguration endpoint = new AwsClientBuilder.EndpointConfiguration(url, region);

        AmazonS3 client = AmazonS3ClientBuilder.standard()
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(endpoint)
//                .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();

        return client;
    }
}
