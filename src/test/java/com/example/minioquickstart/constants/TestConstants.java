package com.example.minioquickstart.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestConstants {

    public static final String POLICY_OBJECT = """
{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"AWS":["*"]},"Action":["s3:GetBucketLocation","s3:ListBucket"],"Resource":["arn:aws:s3:::bucket"]},{"Effect":"Allow","Principal":{"AWS":["*"]},"Action":["s3:GetObject"],"Resource":["arn:aws:s3:::bucket/1219390.jpg*"]}]}""";
    public static final String BUCKET_NAME = "bucket";
    public static final String IMG_FILE_NAME = "1219390.jpg";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String FILE_DATA = "fileData";

    public static final int BUFFER_SIZE = 4096;
}
