package com.joshi.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DocumentProcessedEvent {
    private String fileName;
    private String textExtracted;
    private String classification;
    private LocalDateTime processedTime;

}
