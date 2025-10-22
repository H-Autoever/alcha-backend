package com.carpoor.alchabackend.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventSuddenAccelerationMessage {

    @JsonProperty("vehicle_id")
    private String vehicleId;

    @JsonProperty("vehicle_speed")
    private double vehicleSpeed;

    @JsonProperty("throttle_position")
    private double throttlePosition;

    @JsonProperty("gear_position_mode")
    private String gearPositionMode;

    private String timestamp;
}