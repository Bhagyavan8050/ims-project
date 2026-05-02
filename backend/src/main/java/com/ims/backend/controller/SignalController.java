package com.ims.backend.controller;

import com.ims.backend.dto.SignalDTO;
import com.ims.backend.entity.WorkItem;
import com.ims.backend.model.Signal;
import com.ims.backend.repository.SignalRepository;
import com.ims.backend.repository.WorkItemRepository;
import com.ims.backend.service.SignalQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class SignalController {

    @Autowired
    private SignalQueueService queueService;

    @Autowired
    private SignalRepository signalRepository;

    @Autowired
    private WorkItemRepository workItemRepository;

    @PostMapping("/signal")
    public String receiveSignal(@RequestBody SignalDTO signalDTO) {

        queueService.addSignal(signalDTO);

        Signal s = new Signal();
        s.setComponentId(signalDTO.getComponentId());
        s.setMessage(signalDTO.getMessage());
        s.setTimestamp(java.time.LocalDateTime.now());

        signalRepository.save(s);

        return "Signal queued + saved";
    }

    @PostMapping("/resolve/{id}")
    public String resolveIncident(@PathVariable Long id) {

        WorkItem item = workItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("WorkItem not found"));

        item.setStatus("RESOLVED");
        item.setResolvedTime(System.currentTimeMillis());

        long mttr = item.getResolvedTime() - item.getCreatedTime();
        item.setMttr(mttr);

        workItemRepository.save(item);

        return "Incident resolved successfully. MTTR = " + mttr + " ms";
    }
}