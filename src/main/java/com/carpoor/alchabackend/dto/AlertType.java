package com.carpoor.alchabackend.dto;

import lombok.Getter;

@Getter
public enum AlertType {
    RAMP_PARKING,
    HIGH_TEMPERATURE,
    SUDDEN_UNINTENDED_ACCELERATION,
    COLLISION;
}
