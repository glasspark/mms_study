package com.study.mms.controller;

import com.study.mms.auth.PrincipalDetail;
import com.study.mms.service.LoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminDashBoardController {
    private final LoginLogService loginLogService;

    @ResponseBody
    @GetMapping("/dash/")
    @Operation(summary = "대시보드 방문 로그", description = "관리자 페이지 대시보드 방문자수, 방문횟수 로그 API")
    public ResponseEntity<Map<String, Object>> deleteNotice(@RequestParam Integer year, @RequestParam Integer month
    ) {
        return loginLogService.getUserLoginLog(year, month);
    }


}
