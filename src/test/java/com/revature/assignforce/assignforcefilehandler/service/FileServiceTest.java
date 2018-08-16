package com.revature.assignforce.assignforcefilehandler.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

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

    @Autowired private S3Mock mockServer;
    @Autowired private FileService fileService;

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

        // use service to save file, return key
        key = fileService.save(multipartFile, TEST_CATEGORY, TEST_TRAINER_ID);
    }

    @Test
    public void shouldUploadFile() throws IOException {
        Assert.assertEquals(expectedKey, key);
    }

    @Test
    public void shouldContainMetadataInUploadedFile() throws IOException {
        // retrieve file from bucket using key
        S3Object object = fileService.get(expectedKey);
        ObjectMetadata objectMetadata = object.getObjectMetadata();
        Assert.assertEquals("id_" + TEST_TRAINER_ID,
                objectMetadata.getUserMetaDataOf("x-amz-meta-trainer"));
    }

    @Test
    public void shouldReturnTestFileWhenGettingFileWithKey() throws IOException {
        // retrieve file from bucket using key
        S3Object object = fileService.get(expectedKey);
        Assert.assertNotNull(object);
    }

    @Test
    public void shouldReturnNullWhenGettingFileWithInvalidKey() throws IOException {
        String invalidKey = "someKey";
        Assert.assertNotEquals(invalidKey, key);
        Assert.assertNull(fileService.get(invalidKey));
    }

    @Test
    public void shouldReturnTrueWhenDeletingFileWithKey() throws IOException {
        // delete file from bucket
        boolean result = fileService.delete(key);
        Assert.assertTrue("should return true when delete is successful", result);
    }

    @Test
    public void shouldReturnFalseWhenDeletingFileWithInvalidKey() throws IOException {
        String invalidKey = "someKey";
        //delete file that doesn't exist in bucket
        boolean result = fileService.delete(invalidKey);
        Assert.assertFalse("should return false when delete is unsuccessful", result);
    }

    @After
    public void deinit() {
        // shutdown s3mock
        mockServer.stop();
    }
}
