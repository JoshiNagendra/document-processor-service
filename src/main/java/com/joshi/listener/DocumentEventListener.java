package com.joshi.listener;

import com.joshi.dto.DocumentUploadedEvent;
import com.joshi.service.DocumentProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class DocumentEventListener {

    @Autowired
    private DocumentProcessingService processingService;

    @KafkaListener(topics = "document.uploaded", groupId = "processor-group", containerFactory = "kafkaListenerContainerFactory")
    public void listen(DocumentUploadedEvent event) {
        processingService.process(event);
    }
}
