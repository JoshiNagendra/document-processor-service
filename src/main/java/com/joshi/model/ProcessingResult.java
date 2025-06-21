package com.joshi.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ProcessingResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    private String fileName;

    @Column(name = "text_extracted", columnDefinition = "TEXT")
    private String textExtracted;
    private String classification;
    private LocalDateTime processedAt;


}
