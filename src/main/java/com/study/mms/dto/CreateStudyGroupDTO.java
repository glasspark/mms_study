package com.study.mms.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor // 파라미터 없는 기본 생성자
@AllArgsConstructor // 모든 필드를 받는 생성자 자동 생성
@Data
@Builder
@Schema(description = "스터디 그룹 생성 DTO")
public class CreateStudyGroupDTO {

	private Integer id;
	@NotBlank(message = "스터디 그룹 이름은 필수입니다.")
	@Size(max = 20, message = "스터디 그룹 이름은 최대 20자까지 입력할 수 있습니다.")
	private String name; // 필수 최대 20자
	@Size(max = 200, message = "설명은 최대 200자까지 입력할 수 있습니다.")
	private String description; // 설명 최대 200자
	private String subLeader; // 생략 가능
	@NotNull(message = "최대 인원 수는 필수입니다.")
	@Min(value = 1, message = "최소 인원 수는 1명입니다.")
	@Max(value = 20, message = "최대 인원 수는 20명입니다.")
	private Integer maxMembers; // 1~20명까지
	@Pattern(regexp = "^[^,]*$", message = "태그에 쉼표(,)는 포함될 수 없습니다.")
	private String tag;
}
