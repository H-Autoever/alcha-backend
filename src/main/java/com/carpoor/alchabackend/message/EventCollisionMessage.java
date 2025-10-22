package com.carpoor.alchabackend.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventCollisionMessage {
    @JsonProperty("vehicle_id")
    private String vehicleId;

    private double damage;

    private String timestamp;
}
