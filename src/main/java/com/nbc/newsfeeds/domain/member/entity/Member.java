package com.nbc.newsfeeds.domain.member.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.nbc.newsfeeds.common.audit.BaseEntity;
import com.nbc.newsfeeds.domain.member.constant.MemberResponseCode;
import com.nbc.newsfeeds.domain.member.exception.MemberException;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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

	@ElementCollection
	@Builder.Default
	private List<String> roles = new ArrayList<>();

	public void checkPassword(PasswordEncoder passwordEncoder, String password) {
		if (!passwordEncoder.matches(password, this.password)) {
			throw new MemberException(MemberResponseCode.FAIL_SIGN_IN);
		}
	}

	public void withdraw() {
		this.nickName = "deleted user" + UUID.randomUUID();
		this.isDeleted = true;
		this.birth = LocalDate.of(1900, 1, 1);
		this.phone = "null";
	}

	public void validateNotDeleted() {
		if (this.isDeleted) {
			throw new MemberException(MemberResponseCode.WITHDRAWN_USER);
		}
	}
}
