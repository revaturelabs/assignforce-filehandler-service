package com.revature.assignforce.assignforcefilehandlerservice.controller;

import com.amazonaws.services.s3.model.S3Object;
import com.revature.assignforce.assignforcefilehandlerservice.service.FileService;
import io.findify.s3mock.S3Mock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class FileControllerTests {
    private S3Mock mockServer;

    @Configuration
    static class FileServiceTestConfiguration {

        @Bean
        public FileService fileService() {
            return new FileService();
        }

        @Bean
        public FileController fileController() {
            return new FileController();
        }
    }

    @Autowired
    private FileService fileService;

    @Autowired
    private FileController fileController;

    @Before
    public void init() {
        // create mock s3 server with s3mock
        mockServer = new S3Mock.Builder().withPort(8765).withInMemoryBackend().build();
        mockServer.start();
    }

    @Test
    public void shouldUploadFile() throws IOException {
        // create simple file (?)
        File file = File.createTempFile("test",".tmp");
        String testKey = "test";
        // use controller to upload file
        //Mockito mocks fileService, returns 'test' as file key when it hits fileService.save()
        String key = fileController.addFile(file, testKey);

        Assert.assertEquals(testKey, key);
    }

    @Test
    public void shouldGetFileFromKey() throws IOException {
        // create simple file
        File file = File.createTempFile("test",".tmp");
        // use controller to upload file, return key
        String key = fileController.addFile(file, "test");
        // use key returned by controller to fetch file
        S3Object obj = fileController.getFile(key);

        Assert.assertNotNull(obj);
    }

    @Test
    public void shouldReturnNullWithBadKey() throws IOException {
        //create file to add to bucket
        File file = File.createTempFile("test", ".tmp");
        //use controller to add file
        fileController.addFile(file,"test");
        //use wrong key to get file
        S3Object obj = fileController.getFile("someKey");
        Assert.assertNull(obj);
    }

    @Test
    public void shouldReturnOneWhenDeleteFileFromKey() throws IOException {
        // create simple file
        File file = File.createTempFile("test",".tmp");
        // use controller to upload file, return key
        String key = fileController.addFile(file, "test");
        // use key to delete file
        int result = fileController.deleteFile(key);
        //verify that fileService.delete ran
        Assert.assertEquals(1, result);
    }

    @Test
    public void shouldReturnZeroWhenDeleteFails() throws IOException {
        //create file to have in bucket
        File file = File.createTempFile("test",".tmp");
        //use controller to upload file and return key
        fileController.addFile(file, "test");
        //use wrong key to try to delete file
        int result = fileController.deleteFile("someKey");
        Assert.assertEquals(0, result);
    }

    @After
    public void deinit() {
        // shutdown s3mock
        mockServer.stop();
    }
}
