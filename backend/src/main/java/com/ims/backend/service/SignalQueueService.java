package com.ims.backend.service;

import com.ims.backend.dto.SignalDTO;
import com.ims.backend.worker.SignalWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SignalQueueService {

    private static final Logger log = LoggerFactory.getLogger(SignalQueueService.class);

    @Autowired
    private SignalWorker worker;

    public void addSignal(SignalDTO signal) {
        worker.addSignal(signal);
    }

    @PostConstruct
    public void init() {
        log.info("Starting Signal Worker...");
        worker.startWorker();
    }
}