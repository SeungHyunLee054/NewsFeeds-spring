package com.nbc.newsfeeds.domain.member.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.nbc.newsfeeds.common.jwt.core.JwtService;
import com.nbc.newsfeeds.domain.member.event.MemberWithdrawEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberEventListener {
	private final JwtService jwtService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleMemberWithdrawEvent(MemberWithdrawEvent event) {
		try {
			jwtService.blockAccessToken(event.getAccessToken(), event.getMemberAuth());

			jwtService.deleteRefreshToken(event.getMemberAuth().getEmail());
		} catch (Exception e) {
			log.warn("회원 탈퇴 후 로그아웃 처리 중 오류 발생", e);
		}
	}
}
