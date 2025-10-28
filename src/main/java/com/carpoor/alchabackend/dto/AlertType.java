package com.carpoor.alchabackend.dto;

import lombok.Getter;

@Getter
public enum AlertType {
    RAMP_PARKING,
    HIGH_TEMPERATURE,
    SUDDEN_UNINTENDED_ACCELERATION,
    COLLISION,
    ENGINE_OIL_CHECK,
    ENGINE_CHECK,
    AIRBAG_CHECK,
    COOLANT_CHECK;
}
