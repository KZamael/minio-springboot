package com.example.minioquickstart.utils;

import io.minio.messages.Bucket;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static com.example.minioquickstart.constants.TestConstants.*;

@SpringBootTest
class MinioUtilsTest {

    @Autowired
    MinioUtils minioUtils;

    @Test
    void expectCreateBucket_ToBe_Successful() {
        // arrange & act
        createBucket();

        // assert
        Assertions.assertTrue(minioUtils.bucketExists(BUCKET_NAME));

        // teardown
        tearDownBucket(BUCKET_NAME);
    }

    @Test
    void expectBucketExists_ToBe_Successful() {
        // arrange
        createBucket();

        // act & assert
        Assertions.assertTrue(minioUtils.bucketExists(BUCKET_NAME));

        // teardown
        tearDownBucket(BUCKET_NAME);
    }

    @Test
    void expectBucketExists_NullBucketName_ToReturn_MustNotBeNull() {
        // arrange
        createBucket();

        // act
        Exception thrown = Assertions.assertThrows(Exception.class, () -> {
            minioUtils.bucketExists(null);
        });

        // assert
        Assertions.assertEquals("bucket name must not be null.", thrown.getMessage());
    }

    @Test
    void expectGetBucketPolicy_ToReturnEmptyPolicy_Successfully() {
        // arrange
        createBucket();

        // act
        String policy = minioUtils.getBucketPolicy(BUCKET_NAME);

        // assert
        Assertions.assertEquals("", policy);

        // tear down
        tearDownBucket(BUCKET_NAME);
    }

    @Test
    void expectSetBucketPolicy_ToReturn_Successfully() {
        // arrange
        createBucket();

        // Builder for bucket policy settings
        StringBuilder builder = getPolicyStatement();

        // act
        minioUtils.setBucketPolicy(BUCKET_NAME, builder);

        // assert
        String bucketPolicy = minioUtils.getBucketPolicy(BUCKET_NAME);
        Assertions.assertEquals(TEST_POLICY_OBJECT, bucketPolicy);

        // tear down
        tearDownBucket(BUCKET_NAME);
    }

    @Test
    void expectGetAllBuckets_ToReturn_IsEmpty() {
        // arrange & act
        List<Bucket> bucketList = minioUtils.getAllBuckets();

        // assert
        Assertions.assertFalse(bucketList.isEmpty());
    }

    @Test
    void expectGetBucket_ToReturn_ExpectedBucketName() {
        // arrange
        createBucket();

        // act
        Optional<Bucket> bucketList = minioUtils.getBucket(BUCKET_NAME);

        // assert
        Assertions.assertFalse(bucketList.isEmpty());
        Assertions.assertEquals(BUCKET_NAME, bucketList.stream().findFirst().get().name());

        // tear down
        tearDownBucket(BUCKET_NAME);
    }

    @Test
    void expectRemoveBucket_ToDelete_BucketByName() {
        // arrange
        createBucket();

        // act
        minioUtils.removeBucket(BUCKET_NAME);

        // assert
        Assertions.assertFalse(minioUtils.bucketExists(BUCKET_NAME));
    }

    @Test
    void exIsObjectExist_ToReturn_True() throws IOException {
        // arrange
        minioUtils.createBucket(BUCKET_NAME);

        InputStream content = getInputStream(IMG_FILE_NAME);
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                FILE_DATA, IMG_FILE_NAME, "text/plain", content
        );

        String fileName = mockMultipartFile.getOriginalFilename();
        String contentType = mockMultipartFile.getContentType();

        minioUtils.uploadFile(BUCKET_NAME, mockMultipartFile, fileName, contentType);

        // act
        boolean objectExist = minioUtils.isObjectExist(BUCKET_NAME, IMG_FILE_NAME);

        // assert
        Assertions.assertTrue(objectExist);

        // tear down
        tearDownFile(BUCKET_NAME, IMG_FILE_NAME);
        tearDownBucket(BUCKET_NAME);
    }

    @Disabled
    @Test
    void expectIsFolderExist_ToReturn_True() {
        // arrange
        minioUtils.createBucket(BUCKET_NAME);
        minioUtils.createDir(BUCKET_NAME, TESTFOLDER);

        // act
        boolean folderexists = minioUtils.isFolderExist(BUCKET_NAME, TESTFOLDER);

        // assert
        Assertions.assertTrue(folderexists);

        // tear down
        tearDownBucket(BUCKET_NAME);
    }

    private InputStream getInputStream(String fileName) {
        InputStream inputStream = null;
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            inputStream = classLoader.getResourceAsStream(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return inputStream;
    }

    @NotNull
    private static StringBuilder getPolicyStatement() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        builder.append("    \"Statement\": [\n");
        builder.append("        {\n");
        builder.append("            \"Action\": [\n");
        builder.append("                \"s3:GetBucketLocation\",\n");
        builder.append("                \"s3:ListBucket\"\n");
        builder.append("            ],\n");
        builder.append("            \"Effect\": \"Allow\",\n");
        builder.append("            \"Principal\": \"*\",\n");
        builder.append(String.format("            \"Resource\": \"arn:aws:s3:::%s\"\n", BUCKET_NAME));
        builder.append("        },\n");
        builder.append("        {\n");
        builder.append("            \"Action\": \"s3:GetObject\",\n");
        builder.append("            \"Effect\": \"Allow\",\n");
        builder.append("            \"Principal\": \"*\",\n");
        builder.append(String.format("            \"Resource\": \"arn:aws:s3:::%s/%s*\"\n", BUCKET_NAME, IMG_FILE_NAME));
        builder.append("        }\n");
        builder.append("    ],\n");
        builder.append("    \"Version\": \"2012-10-17\"\n");
        builder.append("}\n");
        return builder;
    }

    private void createBucket() {
        minioUtils.createBucket(BUCKET_NAME);
    }

    private void tearDownFile(String bucketName, String fileName) {
        minioUtils.removeFile(bucketName, fileName);
    }

    private void tearDownBucket(String bucketName) {
        minioUtils.removeBucket(bucketName);
    }
}