package com.revature.assignforce.assignforcefilehandlerservice.controller;

import com.revature.assignforce.assignforcefilehandlerservice.service.FileService;
import io.findify.s3mock.S3Mock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;

import static org.mockito.ArgumentMatchers.anyString;


public class FileControllerTests {
    private S3Mock mockServer;

    @Mock
    private FileService fileServiceMock;

    @InjectMocks
    private FileController fileController;

    @Before
    public void init() {
        // create mock s3 server with s3mock
        mockServer = new S3Mock.Builder().withPort(8181).withInMemoryBackend().build();
        mockServer.start();
        // create instance of the controller
        fileController = new FileController();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldUploadFile() {
        // create simple file (?)
        File file = new File("uploadReadTest.txt");
        // use controller to upload file
        //Mockito mocks fileService, returns 'test' as file key when it hits fileService.save()
        Mockito.when(fileServiceMock.save(file)).thenReturn("test");
        String key = fileController.addFile(file);

        Assert.assertEquals(key, "test");
    }

    @Test
    public void shouldGetFileFromKey() {
        // create simple file
        File file = new File("uploadReadTest.txt");
        // use controller to upload file, return key
        Mockito.when(fileServiceMock.save(file)).thenReturn("test");
        String key = fileController.addFile(file);
        // use key returned by controller to fetch file
        Mockito.when(fileServiceMock.get(key)).thenReturn(new File("uploadReadTest.txt"));
        File f = fileController.getFile(key);
        // compare returned file to created file (assert)
        Assert.assertEquals(file, f);
    }

    @Test
    public void shouldDeleteFileFromKey() {
        // create simple file
        File file = new File("uploadReadTest.txt");
        // use controller to upload file, return key
        Mockito.when(fileServiceMock.save(file)).thenReturn("test");
        String key = fileController.addFile(file);
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
