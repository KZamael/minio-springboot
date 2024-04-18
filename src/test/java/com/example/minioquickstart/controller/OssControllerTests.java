package com.example.minioquickstart.controller;

import com.example.minioquickstart.utils.MinioUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static com.example.minioquickstart.constants.TestConstants.*;

@SpringBootTest
class OssControllerTests {


    @Autowired
    private OSSController ossController;

    @Autowired
    MinioUtils minioUtils;

    @Test
    void expectCreateBucket_ToReturn_BucketSuccessfullyCreated() {
        // arrange & act
        String returnedMessage = ossController.createBucket(BUCKET_NAME);

        // assert
        Assertions.assertTrue(minioUtils.bucketExists(BUCKET_NAME));
        Assertions.assertEquals("bucket successfully created", returnedMessage);

        // teardown
        tearDownBucket(BUCKET_NAME);
    }

    @Test
    void expectCreateBucket_UsingNullForBucketName_ToReturn_BucketCreationFailed() {
        // arrange & act
        String returnedMessage = ossController.createBucket(null);

        // assert
        Assertions.assertFalse(minioUtils.bucketExists(BUCKET_NAME));
        Assertions.assertEquals("bucket creation failed", returnedMessage);
    }

    @Test
    void expectUploadFile_ToReturn_UploadSuccess() {
        // arrange
        InputStream content = null;
        try {
            content = getInputStream(IMG_FILE_NAME);

            MockMultipartFile mockMultipartFile = new MockMultipartFile(
                    FILE_DATA, String.format("pics/%s", IMG_FILE_NAME), TEXT_PLAIN, content
            );

            // act
            String uploadedFile = ossController.upload(mockMultipartFile);

            // assert
            Assertions.assertEquals("upload success", uploadedFile);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (content != null) {
                try {
                    content.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    void expectUploadFile_WrongContentType_ToReturn_UploadFail() {
        // arrange
        InputStream content = null;
        try {
            content = getInputStream(IMG_FILE_NAME);

            MockMultipartFile mockMultipartFile = new MockMultipartFile(
                    FILE_DATA, String.format("pics/%s", IMG_FILE_NAME), "text", content
            );

            // act
            String uploadedFile = ossController.upload(mockMultipartFile);

            // assert
            Assertions.assertEquals("upload fail", uploadedFile);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (content != null) {
                try {
                    content.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    void expectUploadImage_ToReturn_UploadSuccess() {
        // arrange
        InputStream content = null;
        try {
            content = getInputStream(IMG_FILE_NAME);

            MockMultipartFile mockMultipartFile = new MockMultipartFile(
                    FILE_DATA, String.format("pics/%s", IMG_FILE_NAME), TEXT_PLAIN, content
            );

            // act
            String uploadedFile = ossController.upload(mockMultipartFile);

            // assert
            Assertions.assertEquals("upload success", uploadedFile);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (content != null) {
                try {
                    content.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    void expectGetFileInfo_ToReturn_Successful() {
        // arrange
        InputStream content;

        try {
            content = getInputStream(IMG_FILE_NAME);
            MockMultipartFile mockMultipartFile = new MockMultipartFile(
                    FILE_DATA, IMG_FILE_NAME, TEXT_PLAIN, content
            );
            ossController.uploadFileWithStaticName(mockMultipartFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // act
        String fileStatusInfoResult = ossController.getFileStatusInfo(IMG_FILE_NAME).substring("ObjectStat".length());;
        String result = fileStatusInfoResult.substring("bucket=".length() + 1, 18);

        // assert
        Assertions.assertEquals("testbucket", result);

        // teardown
        tearDownFile(IMG_FILE_NAME);
    }

    @Test
    void expectUploadFileWithStaticName_ToReturn_UploadFail() {
        // arrange
        try {
            InputStream content = getInputStream(IMG_FILE_NAME);
            MockMultipartFile mockMultipartFile = new MockMultipartFile(
                    FILE_DATA, IMG_FILE_NAME, "text", content
            );

            // act
            String file = ossController.uploadFileWithStaticName(mockMultipartFile);

            // assert
            Assertions.assertEquals("upload fail", file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // teardown
        tearDownFile(IMG_FILE_NAME);
    }

    @Test
    void expectGetPreSignedObjectUrl_ToReturn_Successful() {
        // arrange & act
        String presignedObjectResult = ossController.getPresignedObjectUrl(IMG_FILE_NAME);

        // assert
        Assertions.assertFalse(presignedObjectResult.isEmpty());
    }

    @Test
    void expectDownload_ToReturn_Successful() throws IOException {
        // arrange
        InputStream content = getInputStream(IMG_FILE_NAME);
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                FILE_DATA, IMG_FILE_NAME, TEXT_PLAIN, content
        );

        ossController.uploadFileWithStaticName(mockMultipartFile);
        MockHttpServletResponse response = new MockHttpServletResponse();

        // act
        ossController.download(IMG_FILE_NAME, response);

        // assert
        Assertions.assertTrue(response.isCharset());
        Assertions.assertEquals("application/force-download;charset=UTF-8", response.getContentType());
        Assertions.assertEquals(BUFFER_SIZE, response.getBufferSize());

        // tear down
        tearDownFile(IMG_FILE_NAME);
    }

    @Test
    void expectDownload_ToReturn_DownloadFail() throws IOException {
        // arrange
        InputStream content = getInputStream(IMG_FILE_NAME);
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                FILE_DATA, IMG_FILE_NAME, TEXT_PLAIN, content
        );

        ossController.uploadFileWithStaticName(mockMultipartFile);
        MockHttpServletResponse response = new MockHttpServletResponse();

        // act
        ossController.download(null, response);

        // assert
        // tear down
        tearDownFile(IMG_FILE_NAME);
    }

    @Test
    void expectGetBucketPolicy_ToReturn_Success() throws IOException {
        // arrange
        String returnedMessage = ossController.createBucket(BUCKET_NAME);

        Assertions.assertTrue(minioUtils.bucketExists(BUCKET_NAME));
        Assertions.assertEquals("bucket successfully created", returnedMessage);

        InputStream content = getInputStream(IMG_FILE_NAME);
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                FILE_DATA, IMG_FILE_NAME, TEXT_PLAIN, content
        );

        ossController.uploadFileWithStaticName(mockMultipartFile);

        // Builder for bucket policy settings
        StringBuilder builder = getPolicyStatement();
        minioUtils.setBucketPolicy(BUCKET_NAME, builder);

        // act
        String bucketPolicy = ossController.getBucketPolicy(BUCKET_NAME);

        // assert
        Assertions.assertEquals(TEST_POLICY_OBJECT, bucketPolicy);

        // teardown
        tearDownBucket(BUCKET_NAME);
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

    private void tearDownFile(String fileName) {
        ossController.deleteFile(fileName);
    }

    private void tearDownBucket(String bucketName) {
        minioUtils.removeBucket(bucketName);
    }
}
