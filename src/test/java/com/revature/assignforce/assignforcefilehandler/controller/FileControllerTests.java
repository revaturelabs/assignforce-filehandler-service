package com.revature.assignforce.assignforcefilehandler.controller;

import io.findify.s3mock.S3Mock;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

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

    @Autowired private S3Mock mockServer;
    @Autowired private FileController fileController;

    private static final int TEST_TRAINER_ID = 1;
    private static final String TEST_CATEGORY = "resume";
    private static final String TEST_FILENAME = "test.txt";

    private String key;
    private String expectedKey;
    private MockMultipartFile multipartFile;

    @Before
    public void init() throws IOException {
        // create mock s3 server with s3mock
        mockServer.start();

        expectedKey = TEST_CATEGORY + "/" + TEST_TRAINER_ID + "-" + TEST_FILENAME;

        // create file to upload
        multipartFile = new MockMultipartFile(
                "user-file", TEST_FILENAME, MediaType.TEXT_PLAIN_VALUE,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit.".getBytes());

        // use controller to upload file
        key = fileController.addFile(multipartFile, TEST_CATEGORY, TEST_TRAINER_ID);
    }

    @Test
    public void shouldUploadFile() throws IOException {
        Assert.assertEquals(expectedKey, key);
    }

    @Test
    public void shouldReturnTestFileWhenGettingFileWithKey() throws IOException {
        // use key returned by controller to fetch file
        ResponseEntity<byte[]> obj = fileController.getFile(key);

        Assert.assertNotNull(obj);
        Assert.assertEquals(HttpStatus.OK, obj.getStatusCode());
    }

    @Test
    public void shouldReturnNullWhenGettingFileWithInvalidKey() throws IOException {
        String invalidKey = "someKey";

        // use wrong key to get file
        ResponseEntity<byte[]> obj = fileController.getFile(invalidKey);

        Assert.assertNotEquals(invalidKey, key);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, obj.getStatusCode());
    }

    @Test
    public void shouldReturnTrueWhenDeletingFileWithKey() throws IOException {
        // use key to delete file
        boolean result = fileController.deleteFile(key);

        Assert.assertTrue("should return true when delete is successful", result);
    }

    @Test
    public void shouldReturnNullWhenGettingDeletedFile() throws IOException {
        // use key to delete file
        boolean result = fileController.deleteFile(key);

        // use deleted key to get file
        ResponseEntity<byte[]> obj = fileController.getFile(key);

        Assert.assertTrue(result);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, obj.getStatusCode());
    }

    @Test
    public void shouldReturnFalseWhenDeletingFileWithInvalidKey() throws IOException {
        String invalidKey = "someKey";

        // use wrong key to delete file
        boolean result = fileController.deleteFile("someKey");

        Assert.assertNotEquals(invalidKey, key);
        Assert.assertFalse("should return false when delete is unsuccessful", result);
    }

    @After
    public void deinit() {
        // shutdown s3mock
        mockServer.stop();
    }
}
