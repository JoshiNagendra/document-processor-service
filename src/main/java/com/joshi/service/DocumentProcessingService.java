package com.joshi.service;

import com.joshi.dto.DocumentFailedEvent;
import com.joshi.dto.DocumentProcessedEvent;
import com.joshi.dto.DocumentUploadedEvent;
import com.joshi.model.ProcessingResult;
import com.joshi.repository.ProcessingResultRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DocumentProcessingService {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ProcessingResultRepository repository;

    public void process(DocumentUploadedEvent event) {
        try {
            InputStream stream = s3Service.download(event.getS3Path());
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String content = reader.lines().collect(Collectors.joining(" "));

            String classification = "generic"; // Dummy logic

            ProcessingResult result = new ProcessingResult();
            result.setFileName(event.getFileName());
            result.setTextExtracted(content);
            result.setClassification(classification);
            result.setProcessedAt(LocalDateTime.now());
            repository.save(result);

            DocumentProcessedEvent processedEvent = new DocumentProcessedEvent();
            processedEvent.setFileName(event.getFileName());
            processedEvent.setTextExtracted(content);
            processedEvent.setClassification(classification);
            processedEvent.setProcessedTime(LocalDateTime.now());

            kafkaTemplate.send("document.processed", event.getFileName(), processedEvent);

        } catch (Exception e) {
            log.error("Error processing file: {}", event.getFileName(), e);

            DocumentFailedEvent failedEvent = new DocumentFailedEvent();
            failedEvent.setFileName(event.getFileName());
            failedEvent.setReason(e.getMessage());
            failedEvent.setFailedAt(LocalDateTime.now());

            kafkaTemplate.send("document.failed", event.getFileName(), failedEvent);
        }
    }
}
