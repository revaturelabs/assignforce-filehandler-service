package com.revature.assignforce.service;

import com.amazonaws.services.s3.model.S3Object;
import io.findify.s3mock.S3Mock;
import org.apache.commons.io.FileUtils;
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
public class FileServiceTest {

    @TestConfiguration
    static class FileServiceTestConfiguration {

        @Bean
        public S3Mock mockServer() {
            return new S3Mock.Builder().withPort(8765).withInMemoryBackend().build();
        }
    }

    @Autowired
    private FileService fileService;

    @Autowired
    private S3Mock mockServer;

    @Before
    public void init() {
        // create mock s3 server with s3mock
        mockServer.start();
    }

    @Test
    public void shouldUploadFile() throws IOException {
        //create file to upload
        File file = File.createTempFile("test",".tmp");
        String key = fileService.save(file,"test");
        Assert.assertEquals("test", key);
    }

    @Test
    public void shouldReturnTestFileWhenGettingFileWithKey() throws IOException {
        //create file to upload
        File file = File.createTempFile("test",".tmp");
        //use fileService to add file to bucket
        fileService.save(file,"test");

        //retrieve file from bucket using key
        S3Object object = fileService.get("test");
        File file2 = new File("resources/testFile.txt");

        FileUtils.copyInputStreamToFile(object.getObjectContent(),file2);

        Assert.assertTrue(FileUtils.contentEquals(file,file2));
    }

    @Test
    public void shouldReturnNullWhenGettingFileWithInvalidKey() throws IOException {
        //create file to upload
        File file = File.createTempFile("test",".tmp");
        //add file to bucket
        fileService.save(file, "test");

        Assert.assertNull(fileService.get("someKey"));
    }

    @Test
    public void shouldReturnTrueWhenDeletingFileWithKey() throws IOException {
        //create file to upload
        File file = File.createTempFile("test",".tmp");

        //use fileService to add file to bucket
        String key = fileService.save(file, "test");

        //delete file from bucket
        boolean result = fileService.delete(key);

        Assert.assertTrue("should return true when delete is successful", result);
    }

    @Test
    public void shouldReturnFalseWhenDeletingFileWithInvalidKey() throws IOException {
        //create file and save file to establish bucket
        File file = File.createTempFile("test",".tmp");
        fileService.save(file, "test");

        //delete file that doesn't exist in bucket
        boolean result = fileService.delete("someKey");

        Assert.assertFalse("should return false when delete is unsuccessful", result);
    }

    @After
    public void deinit() {
        // shutdown s3mock
        mockServer.stop();
    }
}
