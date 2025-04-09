package com.nbc.newsfeeds.domain.feed.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeedRequestDto {

	@NotBlank(message = "제목은 필수입니다.")
	@Size(max = 50, message = "제목은 50자 내로 작성가능합니다")
	private String title;

	@NotBlank(message = "내용은 필수입니다.")
	@Size(max = 1000, message = "내용은 1000자 이내로 작성가능")
	private String content;
}
