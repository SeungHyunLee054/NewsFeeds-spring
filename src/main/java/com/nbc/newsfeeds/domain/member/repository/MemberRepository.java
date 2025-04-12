package com.nbc.newsfeeds.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nbc.newsfeeds.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	boolean existsByEmail(String email);

	boolean existsByNickName(String nickName);

	@EntityGraph(attributePaths = {"roles"})
	Optional<Member> findWithRolesByEmail(String email);
}
