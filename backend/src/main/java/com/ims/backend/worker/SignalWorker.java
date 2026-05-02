package com.ims.backend.worker;

import com.ims.backend.dto.SignalDTO;
import com.ims.backend.entity.WorkItem;
import com.ims.backend.repository.WorkItemRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class SignalWorker {

    private final BlockingQueue<SignalDTO> queue = new LinkedBlockingQueue<>();
    private final ConcurrentHashMap<String, Long> debounceMap = new ConcurrentHashMap<>();

    @Autowired
    private WorkItemRepository workItemRepository;

    // Auto-start worker when Spring boots
    @PostConstruct
    public void init() {
        startWorker();
    }

    // Add signal to queue
    public void addSignal(SignalDTO signal) {
        if (signal == null || signal.getComponentId() == null) {
            System.out.println("Invalid signal received");
            return;
        }
        queue.add(signal);
    }

    // Worker thread
    public void startWorker() {
        Thread workerThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    SignalDTO signal = queue.take();
                    processSignal(signal);
                } catch (Exception e) {
                    System.err.println("Error processing signal: " + e.getMessage());
                }
            }
        });

        workerThread.setDaemon(true);
        workerThread.start();
    }

    // Debounce + processing logic
    private void processSignal(SignalDTO signal) {

        String componentId = signal.getComponentId();
        long currentTime = System.currentTimeMillis();

        Long lastTime = debounceMap.get(componentId);

        // Ignore if within 5 seconds (debouncing)
        if (lastTime != null && (currentTime - lastTime) < 5000) {
            System.out.println("Debounced signal for: " + componentId);
            return;
        }

        debounceMap.put(componentId, currentTime);

        //  CREATE INCIDENT (FIXED)
        WorkItem workItem = new WorkItem();
        workItem.setComponentId(componentId);

        //  IMPORTANT FIX
        workItem.setStatus("OPEN");   // instead of CREATED

        workItem.setCreatedTime(currentTime);

        workItemRepository.save(workItem);

        System.out.println("🚨 Work Item created (OPEN) for: " + componentId);
    }
}