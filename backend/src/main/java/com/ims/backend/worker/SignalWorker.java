package com.ims.backend.worker;

import com.ims.backend.dto.SignalDTO;
import com.ims.backend.entity.WorkItem;
import com.ims.backend.mongo.SignalLog;
import com.ims.backend.repository.WorkItemRepository;
import com.ims.backend.repository.SignalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class SignalWorker {

    private final BlockingQueue<SignalDTO> queue = new LinkedBlockingQueue<>();

    @Autowired
    private WorkItemRepository workItemRepository;

    @Autowired
    private SignalRepository signalRepository;

    private static class WindowData {
        String incidentId;
        long startTime;
    }

    private final ConcurrentHashMap<String, WindowData> windowMap = new ConcurrentHashMap<>();
    private static final long WINDOW_TIME = 10000; // 10 sec

    public void addSignal(SignalDTO signal) {
        if (signal == null || signal.getComponentId() == null) {
            return;
        }
        queue.add(signal);
    }

    public void startWorker() {

        Thread workerThread = new Thread(() -> {

            System.out.println("Signal Worker Started...");

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    SignalDTO signal = queue.take();
                    processSignal(signal);
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }
        });

        workerThread.setDaemon(true);
        workerThread.setName("signal-worker");
        workerThread.start();
    }

    private void processSignal(SignalDTO signal) {

        String componentId = signal.getComponentId();
        long now = System.currentTimeMillis();

        WindowData window = windowMap.compute(componentId, (k, existing) -> {

            if (existing == null || (now - existing.startTime) > WINDOW_TIME) {
                WindowData w = new WindowData();
                w.startTime = now;
                return w;
            }
            return existing;
        });

        if (window.incidentId == null) {

            WorkItem workItem = new WorkItem();
            workItem.setComponentId(componentId);
            workItem.setStatus("OPEN");
            workItem.setCreatedTime(now);

            workItem = workItemRepository.save(workItem);

            window.incidentId = workItem.getId().toString();

            System.out.println(" INCIDENT CREATED: " + window.incidentId);
        }

        SignalLog log = new SignalLog();
        log.setIncidentId(window.incidentId);
        log.setComponentId(componentId);
        log.setPayload(signal.getMessage());
        log.setTimestamp(now);

        signalRepository.save(log);

        System.out.println(" SIGNAL LINKED: " + window.incidentId);
    }
}