package com.nbc.newsfeeds.domain.comment.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.nbc.newsfeeds.common.response.CommonResponse;
import com.nbc.newsfeeds.common.response.CommonResponses;
import com.nbc.newsfeeds.domain.comment.code.CommentExceptionCode;
import com.nbc.newsfeeds.domain.comment.code.CommentSuccessCode;
import com.nbc.newsfeeds.domain.comment.dto.request.CommentCreateRequest;
import com.nbc.newsfeeds.domain.comment.dto.request.CommentUpdateRequest;
import com.nbc.newsfeeds.domain.comment.dto.response.CommentDetailAndUpdateResponse;
import com.nbc.newsfeeds.domain.comment.dto.response.CommentListFindResponse;
import com.nbc.newsfeeds.domain.comment.entity.Comment;
import com.nbc.newsfeeds.domain.comment.exception.CommentException;
import com.nbc.newsfeeds.domain.comment.repository.CommentRepository;
import com.nbc.newsfeeds.domain.feed.code.FeedExceptionCode;
import com.nbc.newsfeeds.domain.feed.entity.Feed;
import com.nbc.newsfeeds.domain.feed.exception.FeedBizException;
import com.nbc.newsfeeds.domain.feed.repository.FeedRepository;
import com.nbc.newsfeeds.domain.member.auth.MemberAuth;
import com.nbc.newsfeeds.domain.member.entity.Member;
import com.nbc.newsfeeds.domain.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

	@InjectMocks
	private CommentService commentService;
	@Mock
	private CommentRepository commentRepository;
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private FeedRepository feedRepository;
	private Member member;
	private Feed feed;
	private MemberAuth authUser;
	private Comment comment;

	@BeforeEach
	void setUp() {
		authUser = MemberAuth.builder().id(1L).email("user@email.com").roles(List.of("ROLE_USER")).build();

		member = Member.builder().id(1L).email("user@email.com").password("1234").build();

		feed = Feed.builder().id(1L).commentCount(1).build();

		comment = Comment.builder().id(1L).content("댓글 내용").member(member).feed(feed).build();
	}

	@Nested
	@DisplayName("댓글 생성 테스트")
	class CreateCommentTest {

		@Test
		@DisplayName("댓글 생성 성공")
		void createComment_success() {
			// given
			CommentCreateRequest request = new CommentCreateRequest("댓글 내용");

			given(memberRepository.findById(authUser.getId())).willReturn(Optional.of(member));
			given(feedRepository.findById(1L)).willReturn(Optional.of(feed));

			// when
			commentService.createComment(1L, request, authUser);

			// then
			verify(commentRepository, times(1)).save(any(Comment.class));
		}

		@Test
		@DisplayName("댓글 생성 실패 - 존재하지 않는 사용자")
		void createComment_fail_memberNotFound() {
			// given
			CommentCreateRequest request = new CommentCreateRequest("댓글 내용");

			given(memberRepository.findById(authUser.getId()))
				.willReturn(Optional.empty());

			// when
			CommentException exception = assertThrows(CommentException.class,
				() -> commentService.createComment(1L, request, authUser));

			// then
			assertAll(
				() -> assertEquals(CommentExceptionCode.MEMBER_NOT_FOUND.getHttpStatus(), exception.getHttpStatus()),
				() -> assertEquals(CommentExceptionCode.MEMBER_NOT_FOUND.getMessage(),
					exception.getResponseCode().getMessage())
			);
		}

		@Test
		@DisplayName("댓글 생성 실패 - 존재하지 않는 게시글")
		void createComment_fail_feedNotFound() {
			// given
			CommentCreateRequest request = new CommentCreateRequest("댓글 내용");

			given(memberRepository.findById(authUser.getId()))
				.willReturn(Optional.of(member));
			given(feedRepository.findById(1L))
				.willReturn(Optional.empty());

			// when
			FeedBizException exception = assertThrows(FeedBizException.class,
				() -> commentService.createComment(1L, request, authUser));

			// then
			assertAll(
				() -> assertEquals(FeedExceptionCode.FEED_NOT_FOUND.getHttpStatus(), exception.getHttpStatus()),
				() -> assertEquals(FeedExceptionCode.FEED_NOT_FOUND.getMessage(),
					exception.getResponseCode().getMessage())
			);
		}

	}

	@Nested
	@DisplayName("댓글 단건 조회 테스트")
	class GetCommentByIdTest {

		@Test
		@DisplayName("댓글 단건 조회 성공")
		void getCommentById_success() {
			//given
			Long commentId = 1L;

			given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

			// when
			CommonResponse<CommentDetailAndUpdateResponse> response = commentService.getCommentById(commentId);

			// then
			assertThat(response.getStatusCode()).isEqualTo(
				CommentSuccessCode.COMMENT_GET_SUCCESS.getHttpStatus().value());

			assertThat(response.getResult()).isInstanceOf(CommentDetailAndUpdateResponse.class);

			CommentDetailAndUpdateResponse detail = response.getResult();

			assertThat(detail.getCommentId()).isEqualTo(comment.getId());
			assertThat(detail.getContent()).isEqualTo(comment.getContent());
			assertThat(detail.getMemberId()).isEqualTo(member.getId());

			verify(commentRepository, times(1)).findById(commentId);
		}

		@Test
		@DisplayName("댓글 단건 조회 실패 - 존재하지 않는 댓글")
		void getCommentById_fail_commentNotFound() {
			// given
			Long commentId = 999L;
			given(commentRepository.findById(commentId))
				.willReturn(Optional.empty());

			// when
			CommentException exception = assertThrows(CommentException.class,
				() -> commentService.getCommentById(commentId));

			// then
			assertAll(
				() -> assertEquals(CommentExceptionCode.COMMENT_NOT_FOUND.getHttpStatus(), exception.getHttpStatus()),
				() -> assertEquals(CommentExceptionCode.COMMENT_NOT_FOUND.getMessage(),
					exception.getResponseCode().getMessage())
			);
		}

	}

	@Nested
	@DisplayName("게시글 id로 댓글 조회 테스트")
	class GetCommentByFeedIdTest {
		@Test
		@DisplayName("게시글 id로 댓글 조회 성공")
		void getCommentsByFeedId_success() {
			//given
			Long feedId = 1L;
			Pageable pageable = PageRequest.of(0, 10);

			List<Comment> comments = List.of(Comment.builder().content("첫번째 댓글").member(member).feed(feed).build(),

				Comment.builder().content("두번째 댓글").member(member).feed(feed).build());

			Page<Comment> commentPage = new PageImpl<>(comments, pageable, comments.size());

			given(commentRepository.findAllByFeedId(feedId, pageable)).willReturn(commentPage);
			given(feedRepository.findById(feedId)).willReturn(Optional.of(feed));

			// when
			CommonResponses<CommentListFindResponse.CommentListItem> response = commentService.getCommentsByFeedId(
				feedId,
				pageable);

			// then
			assertThat(response.getStatus()).isEqualTo(CommentSuccessCode.COMMENT_LIST_SUCCESS.getHttpStatus().value());
			assertThat(response.getResult().getContent()).hasSize(2);
			assertThat(response.getResult().getContent().get(0).getContent()).isEqualTo("첫번째 댓글");
			assertThat(response.getResult().getContent().get(1).getContent()).isEqualTo("두번째 댓글");

			verify(feedRepository, times(1)).findById(feedId);
			verify(commentRepository, times(1)).findAllByFeedId(feedId, pageable);
		}

		@Test
		@DisplayName("댓글 목록 조회 실패 - 존재하지 않는 게시글")
		void getCommentsByFeedId_fail_feedNotFound() {
			// given
			Long feedId = 999L;
			Pageable pageable = PageRequest.of(0, 10);

			given(feedRepository.findById(feedId)).willReturn(Optional.empty());

			// when
			FeedBizException exception = assertThrows(FeedBizException.class,
				() -> commentService.getCommentsByFeedId(feedId, pageable));

			// then
			assertAll(
				() -> assertEquals(FeedExceptionCode.FEED_NOT_FOUND.getHttpStatus(), exception.getHttpStatus()),
				() -> assertEquals(FeedExceptionCode.FEED_NOT_FOUND.getMessage(),
					exception.getResponseCode().getMessage())
			);
		}

	}

	@Nested
	@DisplayName("댓글 수정 테스트")
	class UpdateCommentTest {
		@Test
		@DisplayName("댓글 수정 성공")
		void updateComment_success() {
			// given
			Long commentId = 1L;

			CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글 내용");

			given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

			// when
			CommonResponse<CommentDetailAndUpdateResponse> response = commentService.updateComment(commentId, request,
				authUser);

			// then
			assertThat(response.getStatusCode()).isEqualTo(
				CommentSuccessCode.COMMENT_UPDATE_SUCCESS.getHttpStatus().value());

			assertThat(response.getResult().getCommentId()).isEqualTo(commentId);
			assertThat(response.getResult().getContent()).isEqualTo("수정된 댓글 내용");
			assertThat(comment.getContent()).isEqualTo("수정된 댓글 내용");

			verify(commentRepository, times(1)).findById(commentId);
		}

		@Test
		@DisplayName("댓글 수정 실패 - 존재하지 않는 댓글")
		void updateComment_fail_commentNotFound() {
			// given
			Long commentId = 999L;
			CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글 내용");

			given(commentRepository.findById(commentId)).willReturn(Optional.empty());

			// when
			CommentException exception = assertThrows(CommentException.class,
				() -> commentService.updateComment(commentId, request, authUser));

			// then
			assertAll(
				() -> assertEquals(CommentExceptionCode.COMMENT_NOT_FOUND.getHttpStatus(), exception.getHttpStatus()),
				() -> assertEquals(CommentExceptionCode.COMMENT_NOT_FOUND.getMessage(),
					exception.getResponseCode().getMessage())
			);
		}

		@Test
		@DisplayName("댓글 수정 실패 - 작성자가 아님")
		void updateComment_fail_unauthorized() {
			// given
			Long commentId = 1L;
			CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글 내용");

			// 다른 사람으로 설정 (authUser 의 id = 2L)
			MemberAuth otherUser = MemberAuth.builder()
				.id(2L)
				.email("hacker@email.com")
				.roles(List.of("ROLE_USER"))
				.build();

			given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

			// when
			CommentException exception = assertThrows(CommentException.class,
				() -> commentService.updateComment(commentId, request, otherUser));

			// then
			assertAll(
				() -> assertEquals(CommentExceptionCode.UNAUTHORIZED_ACCESS.getHttpStatus(), exception.getHttpStatus()),
				() -> assertEquals(CommentExceptionCode.UNAUTHORIZED_ACCESS.getMessage(),
					exception.getResponseCode().getMessage())
			);
		}
	}

	@Nested
	@DisplayName("댓글 삭제 테스트")
	class DeleteCommentTest {
		@Test
		@DisplayName("댓글 삭제 성공")
		void deleteByCommentId_success() {
			//given
			Long commentId = 1L;

			given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

			// when
			CommonResponse<Long> response = commentService.deleteByCommentId(commentId, authUser);

			// then
			assertThat(response.getStatusCode()).isEqualTo(
				CommentSuccessCode.COMMENT_DELETE_SUCCESS.getHttpStatus().value());
			assertThat(response.getResult()).isEqualTo(commentId);

			verify(commentRepository, times(1)).findById(commentId);
			verify(commentRepository, times(1)).deleteById(commentId);
		}

		@Test
		@DisplayName("댓글 삭제 실패 - 존재하지 않는 댓글")
		void deleteByCommentId_fail_commentNotFound() {
			// given
			Long invalidCommentId = 999L;
			given(commentRepository.findById(invalidCommentId)).willReturn(Optional.empty());

			// when
			CommentException exception = assertThrows(CommentException.class,
				() -> commentService.deleteByCommentId(invalidCommentId, authUser));

			// then
			assertAll(
				() -> assertEquals(CommentExceptionCode.COMMENT_NOT_FOUND.getHttpStatus(), exception.getHttpStatus()),
				() -> assertEquals(CommentExceptionCode.COMMENT_NOT_FOUND.getMessage(),
					exception.getResponseCode().getMessage())
			);
		}

		@Test
		@DisplayName("댓글 삭제 실패 - 작성자가 아님")
		void deleteByCommentId_fail_unauthorized() {
			// given
			Long commentId = 1L;
			MemberAuth otherUser = MemberAuth.builder()
				.id(2L)
				.email("other@email.com")
				.roles(List.of("ROLE_USER"))
				.build();

			given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

			// when
			CommentException exception = assertThrows(CommentException.class,
				() -> commentService.deleteByCommentId(commentId, otherUser));

			// then
			assertAll(
				() -> assertEquals(CommentExceptionCode.UNAUTHORIZED_ACCESS.getHttpStatus(), exception.getHttpStatus()),
				() -> assertEquals(CommentExceptionCode.UNAUTHORIZED_ACCESS.getMessage(),
					exception.getResponseCode().getMessage())
			);
		}

	}

}
