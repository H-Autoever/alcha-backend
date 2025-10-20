package com.carpoor.alchabackend.controller;

import com.carpoor.alchabackend.dto.AlertDto;
import com.carpoor.alchabackend.service.AlertCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AlertController {

    private final AlertCacheService alertCacheService;

    @GetMapping("/vehicles/{vehicleId}/alerts")
    public ResponseEntity<List<AlertDto>> getAllAlerts(@PathVariable String vehicleId) {
        return new ResponseEntity<>(alertCacheService.getAll(vehicleId), HttpStatus.OK);
    }
}
