package com.nbc.newsfeeds.domain.feed.service;

import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.nbc.newsfeeds.domain.feed.dto.FeedRequestDto;
import com.nbc.newsfeeds.domain.feed.dto.FeedResponseDto;
import com.nbc.newsfeeds.domain.feed.entity.Feed;
import com.nbc.newsfeeds.domain.feed.repository.FeedRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

public class FeedServiceImpl {
	@Service
	@RequiredArgsConstructor
	public static class FeedserviceImpl implements FeedService {
	
		private final FeedRepository feedRepository;

		@PersistenceContext
		private final EntityManager em;

		@Transactional
		@Override
		public FeedResponseDto createFeed(FeedRequestDto requestDto) {
			Feed feed = Feed.builder()
				.memberId(requestDto.getMemberId())
				.name(requestDto.getName())
				.title(requestDto.getTitle())
				.content(requestDto.getContent())
				.heartCount(0)
				.commentCount(0)
				.isDeleted(false).build();
	
			Feed savedFeed = feedRepository.save(feed);
			return FeedResponseDto.fromEntity(savedFeed);
		}
	
		@Override
		public FeedResponseDto getFeedById(Long feedId) {
			Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
			return FeedResponseDto.fromEntity(feed);
		}
	
		@Override
		public Page<FeedResponseDto> getAllFeed(Pageable pageable) {
			return feedRepository.findAll(pageable).map(FeedResponseDto::fromEntity);
		}

		@Transactional
		@Override
		public void deleteFeed(Long feedId) {
			Feed feed = feedRepository.findById(feedId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
			feed.setIsDeleted(true);
			feedRepository.save(feed);
		}

		@Transactional
		@Override
		public FeedResponseDto updateFeed(Long feedId, FeedRequestDto requestDto) {
			Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."));
			feed.update(requestDto.getTitle(), requestDto.getContent());
			feedRepository.save(feed);
			em.flush();
			return FeedResponseDto.fromEntity(feed);
		}
	}
}
