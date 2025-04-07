package com.nbc.newsfeeds.domain.comment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nbc.newsfeeds.domain.comment.dto.CommentRequestCreate;
import com.nbc.newsfeeds.domain.comment.dto.CommentResponse;
import com.nbc.newsfeeds.domain.comment.entity.Comment;
import com.nbc.newsfeeds.domain.comment.repository.CommentRepository;
import com.nbc.newsfeeds.domain.member.entity.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;

}