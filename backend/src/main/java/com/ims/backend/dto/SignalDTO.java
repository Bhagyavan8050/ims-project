package com.ims.backend.dto;

import lombok.Data;

@Data
public class SignalDTO {

    private String componentId;
    private String message;
    private String severity;   // P0, P1, P2
    private String timestamp;
}