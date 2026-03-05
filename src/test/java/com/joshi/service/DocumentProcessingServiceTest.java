package com.joshi.service;

import com.joshi.dto.DocumentFailedEvent;
import com.joshi.dto.DocumentProcessedEvent;
import com.joshi.dto.DocumentUploadedEvent;
import com.joshi.listener.DocumentEventListener;
import com.joshi.model.ProcessingResult;
import com.joshi.repository.ProcessingResultRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.AssertionsKt.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class DocumentProcessingServiceTest {

    @Mock
    private S3Service s3Service;

    @Mock
    private ProcessingResultRepository processingResultRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private DocumentProcessingService documentProcessingService;

    @Test
    void testProcessSuccess() throws Exception {
        String fileContent = "Test Cases";
        InputStream inputStream = new ByteArrayInputStream(fileContent.getBytes());
        when(s3Service.download("s3/path")).thenReturn(inputStream);

        DocumentUploadedEvent event = new DocumentUploadedEvent();
        event.setFileName("test.txt");
        event.setS3Path("s3/path");

        documentProcessingService.process(event);


        ArgumentCaptor<ProcessingResult> resultCaptor = ArgumentCaptor.forClass(ProcessingResult.class);
        verify(processingResultRepository).save(resultCaptor.capture());
        ProcessingResult savedResult = resultCaptor.getValue();

        assertEquals("test.txt", savedResult.getFileName());
        assertEquals("Test Cases", savedResult.getTextExtracted());
        assertEquals("generic", savedResult.getClassification());
        assertNotNull(savedResult.getProcessedAt());

        ArgumentCaptor<DocumentProcessedEvent> eventCaptor = ArgumentCaptor.forClass(DocumentProcessedEvent.class);
        verify(kafkaTemplate).send(eq("document.processed"), eq("test.txt"), eventCaptor.capture());
        DocumentProcessedEvent processedEvent = eventCaptor.getValue();

        assertEquals("test.txt", processedEvent.getFileName());
        assertEquals("Test Cases", processedEvent.getTextExtracted());
        assertEquals("generic", processedEvent.getClassification());
        assertNotNull(processedEvent.getProcessedTime());
    }

    @Test
    void testProcessFailure() throws Exception {

        when(s3Service.download("bad/path")).thenThrow(new RuntimeException("Download failed"));

        DocumentUploadedEvent event = new DocumentUploadedEvent();
        event.setFileName("bad.txt");
        event.setS3Path("bad/path");

        documentProcessingService.process(event);

        ArgumentCaptor<DocumentFailedEvent> failedCaptor = ArgumentCaptor.forClass(DocumentFailedEvent.class);
        verify(kafkaTemplate).send(eq("document.failed"), eq("bad.txt"), failedCaptor.capture());
        DocumentFailedEvent failedEvent = failedCaptor.getValue();

        assertEquals("bad.txt", failedEvent.getFileName());
        assertEquals("Download failed", failedEvent.getReason());
        assertNotNull(failedEvent.getFailedAt());

        verifyNoInteractions(processingResultRepository);
    }
}
