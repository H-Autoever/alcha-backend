package com.carpoor.alchabackend.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PeriodicAppDataMessage {
    @JsonProperty("vehicle_id")
    private String vehicleId;

    @JsonProperty("location_latitude")
    private Double locationLatitude;

    @JsonProperty("location_longitude")
    private Double locationLongitude;

    @JsonProperty("location_altitude")
    private Double locationAltitude;

    @JsonProperty("temperature_cabin")
    private Double temperatureCabin;

    @JsonProperty("temperature_ambient")
    private Double temperatureAmbient;

    @JsonProperty("battery_voltage")
    private Double batteryVoltage;

    @JsonProperty("tpms_front_left")
    private Integer tpmsFrontLeft;

    @JsonProperty("tpms_front_right")
    private Integer tpmsFrontRight;

    @JsonProperty("tpms_rear_left")
    private Integer tpmsRearLeft;

    @JsonProperty("tpms_rear_right")
    private Integer tpmsRearRight;

    @JsonProperty("accelerometer_x")
    private Double accelerometerX;

    @JsonProperty("accelerometer_y")
    private Double accelerometerY;

    @JsonProperty("accelerometer_z")
    private Double accelerometerZ;

    @JsonProperty("fuel_level")
    private Integer fuelLevel;

    @JsonProperty("timestamp")
    private String timestamp;
}
