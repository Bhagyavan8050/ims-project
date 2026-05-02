package com.ims.backend.service;

import com.ims.backend.dto.SignalDTO;
import com.ims.backend.worker.SignalWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class SignalQueueService {

    @Autowired
    private SignalWorker worker;

    public void addSignal(SignalDTO signal) {
        worker.addSignal(signal);
    }

    @PostConstruct
    public void init() {
        worker.startWorker();
    }
}