package com.nbc.newsfeeds.domain.member.dto.request;

import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberUpdateDto {
	private String nickName;

	@Valid
	private PasswordUpdateForm passwordUpdateForm;

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class PasswordUpdateForm {
		@NotBlank
		@Size(min = 8)
		private String password;

		@NotBlank
		@Size(min = 8)
		private String newPassword;

		public boolean isSameAsCurrentPassword(String encodedPassword, PasswordEncoder passwordEncoder) {
			return passwordEncoder.matches(this.newPassword, encodedPassword);
		}
	}
}
