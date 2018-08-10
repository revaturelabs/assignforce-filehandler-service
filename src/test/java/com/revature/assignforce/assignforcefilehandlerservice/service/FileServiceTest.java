package com.revature.assignforce.assignforcefilehandlerservice.service;

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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class FileServiceTest {
    @Configuration
    static class FileServiceTestConfiguration {

        @Bean
        public FileService fileServiceMock() {
            return new FileService();
        }

        @Bean
        public S3Mock mockServer() {
            return new S3Mock.Builder().withPort(8181).withInMemoryBackend().build();
        }
    }

    @Autowired
    private FileService fileService;

    @Autowired
    private S3Mock mockServer;

    @Before
    public void init() {
        // create mock s3 server with s3mock
        mockServer = new S3Mock.Builder().withPort(8765).withInMemoryBackend().build();
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
    public void shouldGetFileFromKey() throws IOException {
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
    public void shouldReturnNullWhenUsingWrongKey() throws IOException {
        //create file to upload
        File file = File.createTempFile("test",".tmp");
        //add file to bucket
        fileService.save(file, "test");

        Assert.assertNull(fileService.get("someKey"));
    }

    @Test
    public void shouldDeleteFileFromKey() throws IOException {
        //create file to upload
        File file = File.createTempFile("test",".tmp");

        //use fileService to add file to bucket
        String key = fileService.save(file, "test");

        //delete file from bucket
        int result = fileService.delete(key);

        Assert.assertEquals(1, result);
    }

    @Test
    public void shouldReturnZeroWhenDeletingFileThatDoesNotExist() throws IOException {
        //create file and save file to establish bucket
        File file = File.createTempFile("test",".tmp");
        fileService.save(file, "test");

        //delete file that doesn't exist in bucket
        int result = fileService.delete("someKey");

        Assert.assertEquals(0, result);
    }

    @After
    public void deinit() {
        // shutdown s3mock
        mockServer.stop();
    }
}
