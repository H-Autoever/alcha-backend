package com.carpoor.alchabackend.message;

import lombok.Data;

@Data
public class RealtimeAppDataMessage {
    private String vehicleId;
    private int speed;
}