package com.example.minioquickstart.controller;

import com.example.minioquickstart.configs.MinioConfig;
import com.example.minioquickstart.utils.MinioUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Slf4j
@RestController
@RequestMapping("/oss")
public class OSSController {

    @Autowired
    private MinioUtils minioUtils;

    @Autowired
    private MinioConfig minioConfig;

    @PostMapping("createBucket")
    public String createBucket(@RequestParam("bucket") String bucketName) {
        try {
            minioUtils.createBucket(bucketName);
            return "bucket successfully created";

        } catch (Exception e) {
            log.error("Bucket creation failed");
            e.printStackTrace();
            return "bucket creation failed";
        }
    }

    /**
     * file upload
     *
     * @param file
     */
    @PostMapping("upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        try {
            // file name
            String fileName = file.getOriginalFilename();
            String newFileName = System.currentTimeMillis() + "." + StringUtils.substringAfterLast(fileName, ".");
            // type
            String contentType = file.getContentType();
            minioUtils.uploadFile(minioConfig.getBucketName(), file, newFileName, contentType);
            return "upload success";
        } catch (Exception e) {
            e.printStackTrace();
            log.error("upload fail");
            return "upload fail";
        }
    }

    /**
     * delete
     *
     * @param fileName
     */
    @DeleteMapping("/")
    public void deleteFile(@RequestParam("fileName") String fileName) {
        minioUtils.removeFile(minioConfig.getBucketName(), fileName);
    }

    /**
     * get file info
     *
     * @param fileName
     * @return
     */
    @GetMapping("/info")
    public String getFileStatusInfo(@RequestParam("fileName") String fileName) {
        return minioUtils.getFileStatusInfo(minioConfig.getBucketName(), fileName);
    }

    /**
     * get file url
     *
     * @param fileName
     * @return
     */
    @GetMapping("/url")
    public String getPresignedObjectUrl(@RequestParam("fileName") String fileName) {
        return minioUtils.getPresignedObjectUrl(minioConfig.getBucketName(), fileName);
    }

    /**
     * file download
     *
     * @param fileName
     * @param response
     */
    @GetMapping("/download")
    public void download(@RequestParam("fileName") String fileName, HttpServletResponse response) {
        try {
            InputStream fileInputStream = minioUtils.getObject(minioConfig.getBucketName(), fileName);
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.setContentType("application/force-download");
            response.setCharacterEncoding("UTF-8");
            IOUtils.copy(fileInputStream, response.getOutputStream());
        } catch(Exception e) {
            log.error("download fail");
        }
    }

    public String uploadFileWithStaticName(@RequestParam("file") MultipartFile file) {
        try {
            // file name
            String fileName = file.getOriginalFilename();
            // type
            String contentType = file.getContentType();
            minioUtils.uploadFile(minioConfig.getBucketName(), file, fileName, contentType);
            return "upload success";
        } catch (Exception e) {
            e.printStackTrace();
            log.error("upload fail");
            return "upload fail";
        }
    }
}
