package com.nbc.newsfeeds.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberWithdrawDto {
	@NotBlank(message = "비밀번호는 필수 입력값이며 공백이 아니어야 합니다.")
	@Size(min = 8, message = "비밀번호는 8글자 이상이어야 합니다.")
	private String password;
}
