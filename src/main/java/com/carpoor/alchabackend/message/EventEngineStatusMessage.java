package com.carpoor.alchabackend.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventEngineStatusMessage {

    @JsonProperty("vehicle_id")
    private String vehicleId;

    @JsonProperty("vehicle_speed")
    private double vehicleSpeed;

    @JsonProperty("gear_position_mode")
    private String gearPositionMode;

    @JsonProperty("inclination_sensor")
    private double inclinationSensor;

    @JsonProperty("side_brake_status")
    private String sideBrakeStatus;

    @JsonProperty("engine_status_ignition")
    private String engineStatusIgnition;

    private String timestamp;

    @JsonProperty("dct_count")
    private int dctCount;

    @JsonProperty("transmission_gear_change_count")
    private int transmissionGearChangeCount;

    @JsonProperty("abs_activation_count")
    private int absActivationCount;

    @JsonProperty("suspension_shock_count")
    private int suspensionShockCount;

    @JsonProperty("adas_sensor_fault_count")
    private int adasSensorFaultCount;

    @JsonProperty("aeb_activation_count")
    private int aebActivationCount;

    @JsonProperty("total_distance")
    private int totalDistance;

    @JsonProperty("line_departure_warning_cont")
    private int lineDepartureWarningCont;
}
