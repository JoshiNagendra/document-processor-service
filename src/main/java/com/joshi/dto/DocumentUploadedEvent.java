package com.joshi.dto;


import jdk.jfr.DataAmount;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DocumentUploadedEvent {
    private String fileName;
    private String fileType;
    private String s3Path;
    private LocalDateTime uploadTime;

}
