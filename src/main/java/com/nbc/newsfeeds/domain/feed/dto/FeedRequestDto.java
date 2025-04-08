package com.nbc.newsfeeds.domain.feed.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeedRequestDto {

	@NotNull(message = "회원 ID는 필수입니다.")
	private Long memberId; //리팩토링 -> HttpSession이나 @AuthenticationPrincipal으로 받기

	@NotBlank(message = "닉네임은 필수 입니다")
	private String name; //리팩토링 -> HttpSession이나 @AuthenticationPrincipal으로 받기

	@NotBlank(message = "제목은 필수입니다.")
	@Size(max = 50, message = "제목은 50자 내로 작성가능합니다")
	private String title;

	@NotBlank(message = "내용은 필수입니다.")
	@Size(max = 1000, message = "내용은 1000자 이내로 작성가능")
	private String content;
}
