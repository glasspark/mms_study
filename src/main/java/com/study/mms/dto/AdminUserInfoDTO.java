package com.study.mms.dto;

import com.study.mms.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserInfoDTO {
    private Integer id;
    private String username;
    private String nickname;
    private String email;
    private String sns;
    private LocalDateTime create;
    private String img_name;
    private String img_path;
    private Boolean img_type = false;

    // User 엔티티를 DTO로 변환하는 정적 메서드
    public static AdminUserInfoDTO fromEntity(User user) {
        return AdminUserInfoDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .sns(user.getSns())
                .create(user.getCreate())
                .img_name(user.getImg_name())
                .img_path(user.getImg_path())
                .img_type(user.getImg_type())
                .build();
    }

}
