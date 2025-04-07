package com.nbc.newsfeeds.domain.friend.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CursorPageResponse {

	@Schema(description = "다음 페이지에서 사용할 커서")
	private Long nextCursor;

	@Schema(description = "다음 페이지 여부")
	private Boolean hasNext;
}
