package com.carpoor.alchabackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RealtimeAppDataDto {
    @JsonProperty("vehicle_id")
    private String vehicleId;

    @JsonProperty("vehicle_speed")
    private Double vehicleSpeed;

    @JsonProperty("engine_rpm")
    private Integer engineRpm;

    @JsonProperty("engine_status_ignition")
    private String engineStatusIgnition;

    @JsonProperty("throttle_position")
    private Double throttlePosition;

    @JsonProperty("gear_position_mode")
    private String gearPositionMode;

    @JsonProperty("gear_position_current_gear")
    private Integer gearPositionCurrentGear;

    @JsonProperty("engine_temp")
    private Double engineTemp;

    @JsonProperty("coolant_temp")
    private Double coolantTemp;

    @JsonProperty("ev_battery_voltage")
    private Double evBatteryVoltage;

    @JsonProperty("ev_battery_current")
    private Double evBatteryCurrent;

    @JsonProperty("ev_battery_soc")
    private Integer evBatterySoc;
}
