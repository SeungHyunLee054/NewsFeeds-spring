package com.nbc.newsfeeds.domain.feed.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbc.newsfeeds.common.model.request.CursorPageRequest;
import com.nbc.newsfeeds.common.model.response.CursorPageResponse;
import com.nbc.newsfeeds.common.util.CursorPaginationUtil;
import com.nbc.newsfeeds.domain.feed.code.FeedExceptionCode;
import com.nbc.newsfeeds.domain.feed.dto.FeedDeleteResponse;
import com.nbc.newsfeeds.domain.feed.dto.FeedRequestDto;
import com.nbc.newsfeeds.domain.feed.dto.FeedResponseDto;
import com.nbc.newsfeeds.domain.feed.dto.FeedSearchCondition;
import com.nbc.newsfeeds.domain.feed.entity.Feed;
import com.nbc.newsfeeds.domain.feed.exception.FeedBizException;
import com.nbc.newsfeeds.domain.feed.repository.FeedRepository;
import com.nbc.newsfeeds.domain.feed.validator.FeedSearchValidator;
import com.nbc.newsfeeds.domain.member.entity.Member;
import com.nbc.newsfeeds.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

	private final FeedRepository feedRepository;
	private final MemberRepository memberRepository;

	/**
	 * 게시글을 생성
	 *
	 * @param userId 게시글을 작성하는 회원의 ID
	 * @param requestDto 게시글 제목 및 내용이 담긴 요청 DTO
	 * @return 생성된 게시글 정보를 담은 응답 DTO
	 * @throws FeedBizException 존재하지 않는 사용자일 경우 예외
	 * @author 기원
	 */
	@Transactional
	@Override
	public FeedResponseDto createFeed(Long userId, FeedRequestDto requestDto) {
		Member member = memberRepository.findById(userId)
			.orElseThrow(() -> new FeedBizException(FeedExceptionCode.FEED_NOT_FOUND));

		Feed feed = Feed.builder()
			.member(member)
			.title(requestDto.getTitle())
			.content(requestDto.getContent())
			.heartCount(0)
			.commentCount(0)
			.isDeleted(false)
			.build();

		Feed savedFeed = feedRepository.save(feed);
		return FeedResponseDto.fromEntity(savedFeed);
	}

	/**
	 * 게시글 ID를 기반으로 단건 조회
	 *
	 * @param feedId 조회할 게시글 ID
	 * @return 조회된 게시글 정보
	 * @throws FeedBizException 해당 게시글이 존재하지 않을 경우 예외
	 * @author 기원
	 */
	@Transactional(readOnly = true)
	@Override
	public FeedResponseDto getFeedById(Long feedId) {
		Feed feed = feedRepository.findByIdWithMember(feedId)
			.orElseThrow(() -> new FeedBizException(FeedExceptionCode.FEED_NOT_FOUND));

		return FeedResponseDto.fromEntity(feed);
	}

	/**
	 * 커서 기반 게시글 전건 조회
	 *
	 * @param cursorPageRequest 커서 및 페이지 크기를 포함한 요청 객체
	 * @return 페이징된 게시글 응답
	 * @author 기원
	 */
	@Transactional(readOnly = true)
	@Override
	public CursorPageResponse<FeedResponseDto> getFeedByCursor(CursorPageRequest cursorPageRequest) {
		List<Feed> feeds = feedRepository.findByCursor(cursorPageRequest.getCursor(), cursorPageRequest.getSize());

		List<FeedResponseDto> dtoList = feeds.stream()
			.map(FeedResponseDto::fromEntity).toList();

		return CursorPaginationUtil.paginate(dtoList, cursorPageRequest.getSize(), FeedResponseDto::getFeedId);
	}

	/**
	 * 게시글 soft delete 처리
	 *
	 * @param userId 삭제를 요청한 사용자 ID
	 * @param feedId 삭제할 게시글 ID
	 * @throws FeedBizException 게시글이 존재하지 않거나, 작성자가 아닌 경우 예외
	 * @author 기원
	 */
	@Transactional
	@Override
	public FeedDeleteResponse deleteFeed(Long userId, Long feedId) {
		Feed feed = feedRepository.findById(feedId)
			.orElseThrow(() -> new FeedBizException(FeedExceptionCode.FEED_NOT_FOUND));

		if (!feed.getMember().getId().equals(userId)) {
			throw new FeedBizException(FeedExceptionCode.NOT_FEED_OWNER);
		}

		feed.markAsDelete();
		feedRepository.save(feed);

		return new FeedDeleteResponse(feed.getId());
	}

	/**
	 * 게시글 수정
	 *
	 * @param userId 수정 요청을 보낸 사용자 ID
	 * @param feedId 수정할 게시글 ID
	 * @param requestDto 수정할 제목과 내용을 담은 요청 DTO
	 * @return 수정된 게시글 정보
	 * @throws FeedBizException 게시글이 존재하지 않거나, 작성자가 아닌 경우 예외
	 * @author 기원
	 */
	@Transactional
	@Override
	public FeedResponseDto updateFeed(Long userId, Long feedId, FeedRequestDto requestDto) {
		Feed feed = feedRepository.findById(feedId)
			.orElseThrow(() -> new FeedBizException(FeedExceptionCode.FEED_NOT_FOUND));

		if (!feed.getMember().getId().equals(userId)) {
			throw new FeedBizException(FeedExceptionCode.NOT_FEED_OWNER);
		}

		feed.update(requestDto.getTitle(), requestDto.getContent());

		return FeedResponseDto.fromEntity(feed);
	}

	/**
	 * 사용자가 좋아요를 누른 게시글 목록을 커서 기반 조회
	 *
	 * @param cursorPageRequest 커서 및 페이지 크기를 포함한 요청 객체
	 * @param memberId 좋아요를 누른 회원 ID
	 * @return 좋아요한 게시글 목록의 커서 기반 응답
	 * @author 기원
	 */
	@Override
	public CursorPageResponse<FeedResponseDto> getLikedFeedByCursor(CursorPageRequest cursorPageRequest,
		Long memberId) {
		List<Feed> feeds = feedRepository.findLikedFeedsByCursor(memberId, cursorPageRequest.getCursor(),
			cursorPageRequest.getSize());

		List<FeedResponseDto> dtoList = feeds.stream()
			.map(FeedResponseDto::fromEntity).toList();


		return CursorPaginationUtil.paginate(dtoList, cursorPageRequest.getSize(), FeedResponseDto::getFeedId);
	}

	@Transactional(readOnly = true)
	@Override
	public CursorPageResponse<FeedResponseDto> searchFeeds(FeedSearchCondition searchCondition) {
		searchCondition.feedSearch();

		FeedSearchValidator.validate(searchCondition);

		List<Feed> feeds = feedRepository.findBySearchCondition(searchCondition);
		List<FeedResponseDto> dtoList = feeds.stream()
			.map(FeedResponseDto::fromEntity)
			.toList();

		return CursorPaginationUtil.paginate(dtoList, searchCondition.getSize(), FeedResponseDto::getFeedId);
	}

}
