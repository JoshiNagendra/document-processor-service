package com.joshi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.InputStream;

@Service
public class S3Service {

    @Autowired
    private S3Client s3Client;

    @Value("${s3.bucket.name}")
    private String bucketName;

    public InputStream download(String s3Path) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Path)
                .build();

        ResponseInputStream<GetObjectResponse> response = s3Client.getObject(request);
        return response;
    }
}
