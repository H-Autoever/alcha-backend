package com.carpoor.alchabackend.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RealtimeAppDataMessage {
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

    @JsonProperty("wheel_speed_front_left")
    private Double wheelSpeedFrontLeft;

    @JsonProperty("wheel_speed_front_right")
    private Double wheelSpeedFrontRight;

    @JsonProperty("wheel_speed_rear_left")
    private Double wheelSpeedRearLeft;

    @JsonProperty("wheel_speed_rear_right")
    private Double wheelSpeedRearRight;

    @JsonProperty("gear_position_mode")
    private String gearPositionMode;

    @JsonProperty("gear_position_current_gear")
    private Integer gearPositionCurrentGear;

    @JsonProperty("gyro_yaw_rate")
    private Double gyroYawRate;

    @JsonProperty("gyro_pitch_rate")
    private Double gyroPitchRate;

    @JsonProperty("gyro_roll_rate")
    private Double gyroRollRate;

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