package com.nbc.newsfeeds.domain.member.entity;

import java.time.LocalDate;

import com.nbc.newsfeeds.common.audit.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Member extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String nickName;

	@Column(unique = true, nullable = false)
	@NonNull
	private String email;

	@Column(nullable = false)
	@NonNull
	private String password;

	@Column(nullable = false)
	private LocalDate birth;

	@Column(nullable = false)
	private String phone;

	@Column(nullable = false)
	private boolean isDeleted;
}
