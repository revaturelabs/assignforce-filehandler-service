package com.revature.assignforce.assignforcefilehandlerservice.controller;

import com.amazonaws.services.s3.model.S3Object;
import io.findify.s3mock.S3Mock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FileControllerTests {

    @TestConfiguration
    static class FileControllerTestConfiguration {

        @Bean
        public S3Mock mockServer() {
            return new S3Mock.Builder().withPort(8765).withInMemoryBackend().build();
        }
    }

    @Autowired
    private S3Mock mockServer;

    @Autowired
    private FileController fileController;

    @Before
    public void init() {
        // create mock s3 server with s3mock
        mockServer.start();
    }

    @Test
    public void shouldUploadFile() throws IOException {
        // create simple file (?)
        File file = File.createTempFile("test",".tmp");
        String testKey = "test";
        // use controller to upload file
        String key = fileController.addFile(file, testKey);

        Assert.assertEquals(testKey, key);
    }

    @Test
    public void shouldReturnTestFileWhenGettingFileWithKey() throws IOException {
        // create simple file
        File file = File.createTempFile("test",".tmp");

        // use controller to upload file, return key
        String key = fileController.addFile(file, "test");

        // use key returned by controller to fetch file
        S3Object obj = fileController.getFile(key);

        Assert.assertNotNull(obj);
    }

    @Test
    public void shouldReturnNullWhenGettingFileWithInvalidKey() throws IOException {
        //create file to add to bucket
        File file = File.createTempFile("test", ".tmp");

        //use controller to add file
        fileController.addFile(file,"test");

        //use wrong key to get file
        S3Object obj = fileController.getFile("someKey");

        Assert.assertNull(obj);
    }

    @Test
    public void shouldReturnTrueWhenDeletingFileWithKey() throws IOException {
        // create simple file
        File file = File.createTempFile("test",".tmp");

        // use controller to upload file, return key
        String key = fileController.addFile(file, "test");

        // use key to delete file
        boolean result = fileController.deleteFile(key);

        Assert.assertTrue("should return true when delete is successful", result);
    }

    @Test
    public void shouldReturnFalseWhenDeletingFileWithInvalidKey() throws IOException {
        //create file to have in bucket
        File file = File.createTempFile("test",".tmp");

        //use controller to upload file and return key
        fileController.addFile(file, "test");

        //use wrong key to try to delete file
        boolean result = fileController.deleteFile("someKey");

        Assert.assertFalse("should return false when delete is unsuccessful", result);
    }

    @After
    public void deinit() {
        // shutdown s3mock
        mockServer.stop();
    }
}
