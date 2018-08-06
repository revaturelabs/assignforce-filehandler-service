package com.revature.assignforce.assignforcefilehandlerservice.controller;

import io.findify.s3mock.S3Mock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class FileControllerTests {
    private S3Mock mockServer;
    private FileController fileController;

    @Before
    public void init() {
        // create mock s3 server with s3mock
        mockServer = new S3Mock.Builder().withPort(8181).withInMemoryBackend().build();
        mockServer.start();
        // create instance of the controller
        fileController = new FileController();
    }

    @Test
    public void shouldUploadFile() {
        // create simple file (?)
        File file = new File(getClass().getResource("uploadReadTest.txt").getFile());
        // use controller to upload file
        String key = fileController.addFile(file);

        Assert.assertEquals("", key);
    }

    @Test
    public void shouldGetFileFromKey() {
        // create simple file
        // use controller to upload file, return key
        // use key returned by controller to fetch file
        // compare returned file to created file (assert)
    }

    @Test
    public void shouldDeleteFileFromKey() {
        // create simple file
        // use controller to upload file, return key
        // use key to delete file
        // get file with key
        // assert does not exist
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
