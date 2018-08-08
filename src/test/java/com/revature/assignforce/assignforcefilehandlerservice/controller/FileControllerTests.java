package com.revature.assignforce.assignforcefilehandlerservice.controller;

import com.amazonaws.services.s3.model.S3Object;
import com.revature.assignforce.assignforcefilehandlerservice.service.FileService;
import io.findify.s3mock.S3Mock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class FileControllerTests {
    private S3Mock mockServer;

    @Configuration
    static class FileServiceTestConfiguration {

        @Bean
        public FileService fileServiceMock() {
            return Mockito.mock(FileService.class);
        }

        @Bean
        public FileController fileController() {
            return new FileController();
        }
    }

    @Autowired
    private FileService fileServiceMock;

    @Autowired
    private FileController fileController;

    @Before
    public void init() {
        // create mock s3 server with s3mock
        mockServer = new S3Mock.Builder().withPort(8181).withInMemoryBackend().build();
        mockServer.start();
    }

    @Test
    public void shouldUploadFile() {
        // create simple file (?)
        File file = new File("uploadReadTest.txt");
        String testKey = "test";
        // use controller to upload file
        //Mockito mocks fileService, returns 'test' as file key when it hits fileService.save()
        Mockito.when(fileServiceMock.save(file, testKey)).thenReturn(testKey);
        String key = fileController.addFile(file, testKey);

        Assert.assertEquals(testKey, key);
    }

    //redo test!!!
    @Test
    public void shouldGetFileFromKey() throws IOException {
        // create simple file
        File file = new File("uploadReadTest.txt");
        // use controller to upload file, return key
        Mockito.when(fileServiceMock.save(file,"test")).thenReturn("test");
        String key = fileController.addFile(file, "test");
        // use key returned by controller to fetch file
        Mockito.when(fileServiceMock.get(key)).thenReturn(new S3Object());
        S3Object obj = fileController.getFile(key);

        Assert.assertNotNull(obj);
    }

    @Test
    public void shouldDeleteFileFromKey() {
        // create simple file
        File file = new File("uploadReadTest.txt");
        // use controller to upload file, return key
        Mockito.when(fileServiceMock.save(file, "test")).thenReturn("test");
        String key = fileController.addFile(file, "test");
        // use key to delete file
        fileController.deleteFile(key);
        //verify that fileService.delete ran
        Mockito.verify(fileServiceMock, Mockito.times(1)).delete(anyString());
    }

    @Test
    public void shouldThrowExceptionWhenBadKeyUsed() {
        // fetch non-existent file from mock server
        // check what returns
        // throw if does not exist
    }

    @After
    public void deinit() {
        // shutdown s3mock
        mockServer.stop();
    }
}
