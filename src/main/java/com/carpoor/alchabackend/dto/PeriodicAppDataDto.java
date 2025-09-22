package com.carpoor.alchabackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeriodicAppDataDto {
    @JsonProperty("vehicle_id")
    private String vehicleId;

    @JsonProperty("location_latitude")
    private Double locationLatitude;

    @JsonProperty("location_longitude")
    private Double locationLongitude;

    @JsonProperty("temperature_cabin")
    private Double temperatureCabin;

    @JsonProperty("temperature_ambient")
    private Double temperatureAmbient;

    @JsonProperty("battery_voltage")
    private Double batteryVoltage;

    @JsonProperty("fuel_level")
    private Integer fuelLevel;

    @JsonProperty("tpms_front_left")
    private Integer tpmsFrontLeft;

    @JsonProperty("tpms_front_right")
    private Integer tpmsFrontRight;

    @JsonProperty("tpms_rear_left")
    private Integer tpmsRearLeft;

    @JsonProperty("tpms_rear_right")
    private Integer tpmsRearRight;
}
