package com.study.mms.controller;

import com.study.mms.auth.PrincipalDetail;
import com.study.mms.dto.NoticeDTO;
import com.study.mms.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Id;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Null;
import java.util.Map;

@RestController
@RequiredArgsConstructor // 생성자 의존성 주입
@RequestMapping("/api/admin")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @ResponseBody
    @GetMapping("/users")
    @Operation(summary = "회원 데이터 필터링 및 반환 API", description = "관리자 페이지 회원 데이터 필터링 및 페이징 API")
    public ResponseEntity<Map<String, Object>> getAdminUsersInfo(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page, @RequestParam(value = "type", required = false) String type, @RequestParam(value = "content", required = false) String content) {
        return adminUserService.getUsers(page, type, content);
    }

    @ResponseBody
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "회원 삭제 API", description = "관리자 페이지 회원 삭제 API")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Integer id, HttpServletRequest req) throws Exception {
        return adminUserService.deleteUser(id, req);
    }

}
