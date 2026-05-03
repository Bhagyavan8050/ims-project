package com.ims.backend.controller;

import com.ims.backend.dto.SignalDTO;
import com.ims.backend.entity.WorkItem;
import com.ims.backend.repository.WorkItemRepository;
import com.ims.backend.service.SignalQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class SignalController {

    private static final Logger log = LoggerFactory.getLogger(SignalController.class);

    @Autowired
    private SignalQueueService queueService;

    @Autowired
    private WorkItemRepository workItemRepository;

    //  RECEIVE SIGNAL (ONLY SEND TO QUEUE)
    @PostMapping("/signal")
    public ResponseEntity<?> receiveSignal(@RequestBody SignalDTO signalDTO) {

        log.info("Received signal: {}", signalDTO);

        if (signalDTO.getComponentId() == null || signalDTO.getComponentId().isEmpty()) {
            return ResponseEntity.badRequest().body("ComponentId is required");
        }

        try {
            // ONLY queue it (worker will handle everything)
            queueService.addSignal(signalDTO);

            log.info("Signal added to queue for componentId={}", signalDTO.getComponentId());

            return ResponseEntity.ok("Signal received and queued");

        } catch (Exception ex) {
            log.error("Error adding signal to queue", ex);
            return ResponseEntity.internalServerError().body("Error processing signal");
        }
    }

    //  RESOLVE INCIDENT (KEEP AS IS BUT CLEANED)
    @PostMapping("/resolve/{id}")
    public ResponseEntity<?> resolveIncident(@PathVariable Long id) {

        log.info("Resolving incident id={}", id);

        try {
            WorkItem item = workItemRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("WorkItem not found"));

            item.setStatus("RESOLVED");
            item.setResolvedTime(System.currentTimeMillis());

            long mttr = item.getResolvedTime() - item.getCreatedTime();
            item.setMttr(mttr);

            workItemRepository.save(item);

            log.info("Incident resolved id={}, MTTR={} ms", id, mttr);

            return ResponseEntity.ok("Resolved. MTTR = " + mttr + " ms");

        } catch (Exception ex) {
            log.error("Error resolving incident id={}", id, ex);
            return ResponseEntity.internalServerError().body("Error resolving incident");
        }
    }
}