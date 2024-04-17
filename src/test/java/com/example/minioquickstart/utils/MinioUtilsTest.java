package com.example.minioquickstart.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.example.minioquickstart.constants.TestConstants.BUCKET_NAME;

@SpringBootTest
class MinioUtilsTest {

    @Autowired
    MinioUtils minioUtils;

    @Test
    void expectCreateBucket_ToBe_Successful() {
        // arrange & act
        minioUtils.createBucket(BUCKET_NAME);

        // assert
        Assertions.assertTrue(minioUtils.bucketExists(BUCKET_NAME));

        // teardown
        tearDownBucket(BUCKET_NAME);
    }

    @Test
    void expectBucketExists_ToBe_Successful() {
        // arrange
        minioUtils.createBucket(BUCKET_NAME);

        // act & assert
        Assertions.assertTrue(minioUtils.bucketExists(BUCKET_NAME));

        // teardown
        tearDownBucket(BUCKET_NAME);
    }

    @Test
    void expectBucketExists_NullBucketName_ToReturn_MustNotBeNull() {
        // arrange
        minioUtils.createBucket(BUCKET_NAME);

        // act
        Exception thrown = Assertions.assertThrows(Exception.class, () -> {
            minioUtils.bucketExists(null);
        });

        // assert
        Assertions.assertEquals("bucket name must not be null.", thrown.getMessage());


    }

    @Test
    void getBucketPolicy() {
    }

    @Test
    void setBucketPolicy() {
    }

    @Test
    void getAllBuckets() {
    }

    @Test
    void getBucket() {
    }

    @Test
    void removeBucket() {
    }

    @Test
    void isObjectExist() {
    }

    @Test
    void isFolderExist() {
    }

    private void tearDownFile(String bucketName, String fileName) {
        minioUtils.removeFile(bucketName, fileName);
    }

    private void tearDownBucket(String bucketName) {
        minioUtils.removeBucket(bucketName);
    }
}