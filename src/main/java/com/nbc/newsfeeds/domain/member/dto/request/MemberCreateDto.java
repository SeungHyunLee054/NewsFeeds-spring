package com.nbc.newsfeeds.domain.member.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberCreateDto {
	@NotBlank(message = "이름은 필수 입력값이며 공백이 아니어야 합니다.")
	private String nickName;

	@NotBlank(message = "이메일은 필수 입력값이며 공백이 아니어야 합니다.")
	@Email(message = "이메일 형식이 잘못되었습니다.")
	private String email;

	@NotBlank(message = "비밀번호는 필수 입력값이며 공백이 아니어야 합니다.")
	@Size(min = 8, message = "비밀번호는 8글자 이상이어야 합니다.")
	private String password;

	@NotNull(message = "생년월일은 필수 입력 값입니다.")
	private LocalDate birth;

	@NotBlank(message = "전화번호는 필수 입력값이며 공백이 아니어야 합니다.")
	@Pattern(regexp = "^[0-9]{10,11}$", message = "전화번호는 '-' 없이 숫자만 10~11자리로 입력해야 합니다.")
	private String phone;
}
