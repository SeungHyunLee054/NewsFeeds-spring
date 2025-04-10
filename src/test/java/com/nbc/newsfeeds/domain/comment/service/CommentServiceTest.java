package com.nbc.newsfeeds.domain.comment.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbc.newsfeeds.common.response.CommonResponse;
import com.nbc.newsfeeds.common.response.CommonResponses;
import com.nbc.newsfeeds.domain.comment.code.CommentSuccessCode;
import com.nbc.newsfeeds.domain.comment.dto.request.CommentCreateRequest;
import com.nbc.newsfeeds.domain.comment.dto.request.CommentUpdateRequest;
import com.nbc.newsfeeds.domain.comment.dto.response.CommentCreateResponse;
import com.nbc.newsfeeds.domain.comment.dto.response.CommentDetailAndUpdateResponse;
import com.nbc.newsfeeds.domain.comment.dto.response.CommentListFindResponse;
import com.nbc.newsfeeds.domain.comment.entity.Comment;
import com.nbc.newsfeeds.domain.comment.repository.CommentRepository;
import com.nbc.newsfeeds.domain.feed.entity.Feed;
import com.nbc.newsfeeds.domain.feed.repository.FeedRepository;
import com.nbc.newsfeeds.domain.member.auth.MemberAuth;
import com.nbc.newsfeeds.domain.member.entity.Member;
import com.nbc.newsfeeds.domain.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

	private final ObjectMapper objectMapper = new ObjectMapper();
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

	@Test
	@DisplayName("댓글 생성 테스트")
	void createComment_success() throws Exception {
		// given
		CommentCreateRequest request = new CommentCreateRequest("댓글 내용");

		given(memberRepository.findById(authUser.getId())).willReturn(Optional.of(member));
		given(feedRepository.findById(1L)).willReturn(Optional.of(feed));

		// when
		CommonResponse<CommentCreateResponse> response = commentService.createComment(1L, request, authUser);

		// then
		verify(commentRepository).save(any(Comment.class));
	}

	@Test
	@DisplayName("댓글 단건 조회 성공 테스트")
	void getCommentById_success() {
		//given
		Long commentId = 1L;

		given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

		// when
		CommonResponse<CommentDetailAndUpdateResponse> response = commentService.getCommentById(commentId);

		// then
		assertThat(response.getStatusCode()).isEqualTo(CommentSuccessCode.COMMENT_GET_SUCCESS.getHttpStatus().value());

		assertThat(response.getResult()).isInstanceOf(CommentDetailAndUpdateResponse.class);

		CommentDetailAndUpdateResponse detail = response.getResult();

		assertThat(detail.getCommentId()).isEqualTo(comment.getId());
		assertThat(detail.getContent()).isEqualTo(comment.getContent());
		assertThat(detail.getMemberId()).isEqualTo(member.getId());

		verify(commentRepository).findById(commentId);
	}

	@Test
	@DisplayName("게시글 id로 댓글 조회 성공 테스트")
	void getCommentsByFeedId_success() throws Exception {
		//given
		Long feedId = 1L;
		Pageable pageable = PageRequest.of(0, 10);

		List<Comment> comments = List.of(Comment.builder().content("첫번째 댓글").member(member).feed(feed).build(),

			Comment.builder().content("두번째 댓글").member(member).feed(feed).build());

		Page<Comment> commentPage = new PageImpl<>(comments, pageable, comments.size());

		given(commentRepository.findAllByFeedId(feedId, pageable)).willReturn(commentPage);
		given(feedRepository.findById(feedId)).willReturn(Optional.of(feed));

		// when
		CommonResponses<CommentListFindResponse.CommentListItem> response = commentService.getCommentsByFeedId(feedId,
			pageable);

		// then
		assertThat(response.getStatus()).isEqualTo(CommentSuccessCode.COMMENT_LIST_SUCCESS.getHttpStatus().value());
		assertThat(response.getResult().getContent()).hasSize(2);
		assertThat(response.getResult().getContent().get(0).getContent()).isEqualTo("첫번째 댓글");
		assertThat(response.getResult().getContent().get(1).getContent()).isEqualTo("두번째 댓글");

		verify(feedRepository).findById(feedId);
		verify(commentRepository).findAllByFeedId(feedId, pageable);
	}

	@Test
	@DisplayName("댓글 수정 성공 테스트")
	void updateComment_success() throws Exception {
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

		verify(commentRepository).findById(commentId);
	}

	@Test
	@DisplayName("댓글 삭제 성공 테스트")
	void deleteByCommentId_success() throws Exception {
		//given
		Long commentId = 1L;

		given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

		// when
		CommonResponse<Long> response = commentService.deleteByCommentId(commentId, authUser);

		// then
		assertThat(response.getStatusCode()).isEqualTo(
			CommentSuccessCode.COMMENT_DELETE_SUCCESS.getHttpStatus().value());
		assertThat(response.getResult()).isEqualTo(commentId);

		verify(commentRepository).findById(commentId);
		verify(commentRepository).deleteById(commentId);

	}

}
