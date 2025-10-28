package com.carpoor.alchabackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertDto {
    private String vehicleId;
    private AlertType alertType;
    private AlertLevel alertLevel;
    private String message;
    private String timestamp;
}
