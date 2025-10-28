package com.carpoor.alchabackend.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventWarningLightMessage {

    @JsonProperty("vehicle_id")
    private String vehicleId;

    private String type; // "engine_oil_check", "engine_check", "airbag_check", "coolant_check"

    private String timestamp;
}
