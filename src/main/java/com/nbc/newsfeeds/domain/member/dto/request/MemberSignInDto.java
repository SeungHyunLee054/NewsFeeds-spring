package com.nbc.newsfeeds.domain.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberSignInDto {
	@NotBlank(message = "이메일은 필수 입력값이며 공백이 아니어야 합니다.")
	@Email(message = "이메일 형식이 잘못되었습니다.")
	private String email;

	@NotBlank(message = "비밀번호는 필수 입력값이며 공백이 아니어야 합니다.")
	@Size(min = 8, message = "비밀번호는 8글자 이상이어야 합니다.")
	private String password;
}
