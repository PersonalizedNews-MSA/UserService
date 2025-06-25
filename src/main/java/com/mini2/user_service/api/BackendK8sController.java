package com.mini2.user_service.api;

import com.mini2.user_service.common.dto.ApiResponseDto;
import com.mini2.user_service.service.probe.ProbeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/backend/user/v1/k8s", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class BackendK8sController {
    private final ProbeService probeService;

    @GetMapping(value = "/liveness")
    public ApiResponseDto<String> liveness() {
        probeService.validateLiveness();
        return ApiResponseDto.defaultOk();
    }

    @GetMapping(value = "/readiness")
    public ApiResponseDto<String> readiness() {
        probeService.validateReadiness();
        return ApiResponseDto.defaultOk();
    }
}
