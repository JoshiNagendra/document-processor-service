package com.joshi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private S3Service s3Service;


    @Test
    void testDownload_returnsStream() {
        String key = "file.pdf";
        ResponseInputStream<GetObjectResponse> mockStream =
                new ResponseInputStream<>(GetObjectResponse.builder().build(),
                        new ByteArrayInputStream("data".getBytes()));

        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(mockStream);

        try {
            s3Service.getClass()
                    .getDeclaredField("bucketName")
                    .set(s3Service, "bucket");
        } catch (Exception ignored) {}

        InputStream result = s3Service.download(key);

        assertNotNull(result);
        verify(s3Client).getObject(any(GetObjectRequest.class));
    }
    @Test
    void testDownload_whenS3ThrowsException() {
        String key = "file.pdf";

        when(s3Client.getObject(any(GetObjectRequest.class)))
                .thenThrow(new RuntimeException("S3 download failed"));

        try {
            var field = s3Service.getClass().getDeclaredField("bucketName");
            field.setAccessible(true);
            field.set(s3Service, "bucket");
        } catch (Exception ignored) {}

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> s3Service.download(key)
        );

        assertTrue(ex.getMessage().contains("S3 download failed"));
        verify(s3Client, times(1)).getObject(any(GetObjectRequest.class));
    }
}
