package com.carpoor.alchabackend.message;

import lombok.Data;

@Data
public class PeriodicAppDataMessage {
    private String vehicleId;
    private int temperature;
}
