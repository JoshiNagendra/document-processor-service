package com.joshi.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DocumentFailedEvent {

    private String fileName;
    private String reason;
    private LocalDateTime failedAt;

}
