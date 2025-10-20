package com.carpoor.alchabackend.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EventMessage {
    @JsonProperty("vehicle_id")
    private String vehicleId;
    
    @JsonProperty("timestamp")
    private String timestamp;
}
