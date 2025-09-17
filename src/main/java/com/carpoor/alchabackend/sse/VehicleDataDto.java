package com.carpoor.alchabackend.sse;

import lombok.Data;

/**
 * 차량 데이터 DTO
 * 데이터 전송 객체 
 */
@Data
public class VehicleDataDto {
    
    private String vehicleId;
    private String status; // driving, parking
    private Object data; // 차량 데이터
    
    // TODO: 구체적인 필드들 추가
}
