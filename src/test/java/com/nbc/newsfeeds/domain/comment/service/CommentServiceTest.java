package com.nbc.newsfeeds.domain.comment.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CommentServiceTest {

	private CommentService commentService;

	private CommentRepository commentRepository;
	private MemberRepository memberRepository;
	private FeedRepository feedRepository;

	private MemberAuth authUser;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		commentRepository = mock(CommentRepository.class);
		memberRepository = mock(MemberRepository.class);
		feedRepository = mock(FeedRepository.class);

		commentService = new CommentService(
			commentRepository,
			memberRepository,
			feedRepository
		);

		authUser = MemberAuth.builder()
			.id(1L)
			.email("test@email.com")
			.roles(List.of("ROLE_USER"))
			.build();
	}

	@Test
	@DisplayName("댓글 생성 테스트")
	void createComment_success() throws Exception {
		// given
		Long feedId = 1L;
		String content = "댓글 내용";

		CommentCreateRequest request = objectMapper.readValue(
			objectMapper.writeValueAsString(Map.of("content", content)),
			CommentCreateRequest.class
		);

		Member mockMember = Member.builder()
			.id(1L)
			.email("test@email.com")
			.password("test1234")
			.build();

		Feed mockFeed = Feed.builder()
			.id(feedId)
			.build();

		Comment mockComment = Comment.builder()
			.id(100L)
			.content(content)
			.member(mockMember)
			.feed(mockFeed)
			.build();

		when(memberRepository.findById(authUser.getId()))
			.thenReturn(Optional.of(mockMember));

		when(feedRepository.findById(feedId))
			.thenReturn(Optional.of(mockFeed));

		when(commentRepository.save(any(Comment.class)))
			.thenAnswer(invocationOnMock -> {
				Comment saved = invocationOnMock.getArgument(0);
				Field idField = Comment.class.getDeclaredField("id");
				idField.setAccessible(true);
				idField.set(saved, 100L);
				return saved;
			});

		// when
		CommonResponse<CommentCreateResponse> response = commentService.createComment(feedId, request, authUser);

		// then
		assertThat(response.getStatusCode()).isEqualTo(CommentSuccessCode.COMMENT_CREATE_SUCCESS.getHttpStatus().value());
		assertThat(response.getResult().getCommentId()).isEqualTo(mockComment.getId());
		assertThat(response.getResult().getContent()).isEqualTo(mockComment.getContent());
		assertThat(response.getResult().getFeedId()).isEqualTo(feedId);
		assertThat(response.getResult().getMemberId()).isEqualTo(authUser.getId());

		// verify
		verify(memberRepository).findById(authUser.getId());
		verify(feedRepository).findById(feedId);
		verify(commentRepository).save(any(Comment.class));
	}

	@Test
	@DisplayName("댓글 단건 조회 테스트")
	void getCommentById_success(){
		//given
		Long commentId = 1L;

		Member member = Member.builder()
			.id(1L)
			.email("user@email.com")
			.password("1234")
			.build();

		Feed feed = Feed.builder()
			.id(10L)
			.build();

		Comment comment = Comment.builder()
			.id(commentId)
			.content("조회할 댓글")
			.member(member)
			.feed(feed)
			.build();

		when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

		// when
		CommonResponse<CommentDetailAndUpdateResponse> response = commentService.getCommentById(commentId);

		// then
		assertThat(response.getStatusCode()).isEqualTo(CommentSuccessCode.COMMENT_GET_SUCCESS.getHttpStatus().value());

		assertThat(response.getResult()).isInstanceOf(CommentDetailAndUpdateResponse.class);

		CommentDetailAndUpdateResponse detail = (CommentDetailAndUpdateResponse) response.getResult();

		assertThat(detail.getCommentId()).isEqualTo(comment.getId());
		assertThat(detail.getContent()).isEqualTo(comment.getContent());
		assertThat(detail.getMemberId()).isEqualTo(member.getId());

		verify(commentRepository).findById(commentId);

	}

	@Test
	@DisplayName("게시글 id로 댓글 조회 테스트")
	void getCommentsByFeedId_success() throws Exception {
		//given
		Long feedId = 1L;
		Pageable pageable = PageRequest.of(0, 10);

		Member member = Member.builder()
			.id(1L)
			.email("user@email.com")
			.password("1234")
			.build();

		Feed feed = Feed.builder()
			.id(feedId)
			.build();

		List<Comment> comments = List.of(
			Comment.builder()
				.id(1L)
				.content("첫번째 댓글")
				.member(member)
				.feed(feed)
				.build(),

			Comment.builder()
				.id(2L)
				.content("두번째 댓글")
				.member(member)
				.feed(feed)
				.build()
			);

		Page<Comment> commentPage = new PageImpl<>(comments, pageable, comments.size());

		when(commentRepository.findAllByFeedId(feedId, pageable)).thenReturn(commentPage);

		when(feedRepository.findById(feedId)).thenReturn(Optional.of(feed));

		// when
		CommonResponses<CommentListFindResponse.CommentListItem> response = commentService.getCommentsByFeedId(feedId, pageable);

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

		Member member = Member.builder()
			.id(1L)
			.email("user@email.com")
			.password("1234")
			.build();

		Feed feed = Feed.builder()
			.id(10L)
			.build();

		Comment comment = Comment.builder()
			.id(commentId)
			.content("기존 댓글 내용")
			.member(member)
			.feed(feed)
			.build();

		MemberAuth authUser = MemberAuth.builder()
			.id(1L) // 👈 comment.member.id와 반드시 같아야 함
			.email("user@email.com")
			.roles(List.of("ROLE_USER"))
			.build();

		CommentUpdateRequest request = objectMapper.readValue(
			objectMapper.writeValueAsString(Map.of("content", "수정된 댓글 내용")),
			CommentUpdateRequest.class
		);

		when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

		when(memberRepository.findById(authUser.getId())).thenReturn(Optional.of(member));

		// when
		CommonResponse<CommentDetailAndUpdateResponse> response = commentService.updateComment(
			commentId, request, authUser);

		// then
		assertThat(response.getStatusCode())
			.isEqualTo(CommentSuccessCode.COMMENT_UPDATE_SUCCESS.getHttpStatus().value());

		assertThat(response.getResult().getCommentId()).isEqualTo(commentId);
		assertThat(response.getResult().getContent()).isEqualTo("수정된 댓글 내용");
		assertThat(comment.getContent()).isEqualTo("수정된 댓글 내용");

		verify(commentRepository).findById(commentId);
	}


	@Test
	@DisplayName("댓글 삭제 테스트")
	void deleteByCommentId_success() throws Exception {
		//given
		Long commentId = 1L;

		Member member = Member.builder()
			.id(1L)
			.email("user@email.com")
			.password("1234")
			.build();

		Feed feed = Feed.builder()
			.id(10L)
			.build();

		Comment comment = Comment.builder()
			.id(commentId)
			.content("삭제할 댓글")
			.member(member)
			.feed(feed)
			.build();

		MemberAuth authUser = MemberAuth.builder()
			.id(1L)
			.email("user@email.com")
			.roles(List.of("ROLE_USER"))
			.build();

		when(memberRepository.findById(authUser.getId())).thenReturn(Optional.of(member));
		when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

		// when
		CommonResponse<Long> response = commentService.deleteByCommentId(commentId, authUser);

		// then
		assertThat(response.getStatusCode()).isEqualTo(CommentSuccessCode.COMMENT_DELETE_SUCCESS.getHttpStatus().value());
		assertThat(response.getResult()).isEqualTo(commentId);

		verify(commentRepository).findById(commentId);
		verify(commentRepository).deleteById(commentId);

	}
}
