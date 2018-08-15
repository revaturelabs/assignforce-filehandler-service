//package com.revature.assignforce.assignforcefilehandler.service;
//
//import com.amazonaws.services.s3.model.ObjectMetadata;
//import com.amazonaws.services.s3.model.S3Object;
//import com.revature.assignforce.assignforcefilehandler.model.FileData;
//import com.revature.assignforce.assignforcefilehandler.model.Metadata;
//import io.findify.s3mock.S3Mock;
//import org.apache.commons.io.FileUtils;
//import org.junit.After;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.io.File;
//import java.io.IOException;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class FileServiceTest {
//
//    @TestConfiguration
//    static class FileServiceTestConfiguration {
//
//        @Bean
//        public S3Mock mockServer() {
//            return new S3Mock.Builder().withPort(8765).withInMemoryBackend().build();
//        }
//    }
//
//    @Autowired
//    private FileService fileService;
//
//    @Autowired
//    private S3Mock mockServer;
//
//    private FileData data;
//    private String expectedKey;
//
//    @Before
//    public void init() throws IOException {
//        // create mock s3 server with s3mock
//        mockServer.start();
//
//        // create file to upload
//        File file = File.createTempFile("test",".tmp");
//
//        expectedKey = "test_uploader_user_" + file.getName();
//
//        Metadata metadata = new Metadata();
//        metadata.setUploader("test_uploader_user");
//
//        data = new FileData();
//        data.setFile(file);
//        data.setMetadata(metadata);
//    }
//
//    @Test
//    public void shouldUploadFile() throws IOException {
//        String key = fileService.save(data);
//        Assert.assertEquals(expectedKey, key);
//    }
//
//    @Test
//    public void shouldContainMetadataInUploadedFile() throws IOException {
//        // use fileService to add file to bucket
//        fileService.save(data);
//
//        // retrieve file from bucket using key
//        S3Object object = fileService.get(expectedKey);
//        ObjectMetadata objectMetadata = object.getObjectMetadata();
//
//        Assert.assertEquals(data.getMetadata().getUploader(), objectMetadata.getUserMetaDataOf("x-amz-meta-uploader"));
//    }
//
//    @Test
//    public void shouldReturnTestFileWhenGettingFileWithKey() throws IOException {
//        // use fileService to add file to bucket
//        fileService.save(data);
//
//        // retrieve file from bucket using key
//        S3Object object = fileService.get(expectedKey);
//        File file = new File("resources/testFile.txt");
//
//        FileUtils.copyInputStreamToFile(object.getObjectContent(), file);
//
//        Assert.assertTrue(FileUtils.contentEquals(data.getFile(), file));
//
//        file.delete();
//    }
//
//    @Test
//    public void shouldReturnNullWhenGettingFileWithInvalidKey() throws IOException {
//        // use fileService to add file to bucket
//        fileService.save(data);
//
//        Assert.assertNull(fileService.get("someKey"));
//    }
//
//    @Test
//    public void shouldReturnTrueWhenDeletingFileWithKey() throws IOException {
//        // use fileService to add file to bucket
//        String key = fileService.save(data);
//
//        //delete file from bucket
//        boolean result = fileService.delete(key);
//
//        Assert.assertTrue("should return true when delete is successful", result);
//    }
//
//    @Test
//    public void shouldReturnFalseWhenDeletingFileWithInvalidKey() throws IOException {
//        // use fileService to add file to bucket
//        fileService.save(data);
//
//        //delete file that doesn't exist in bucket
//        boolean result = fileService.delete("someKey");
//
//        Assert.assertFalse("should return false when delete is unsuccessful", result);
//    }
//
//    @After
//    public void deinit() {
//        // shutdown s3mock
//        mockServer.stop();
//    }
//}
