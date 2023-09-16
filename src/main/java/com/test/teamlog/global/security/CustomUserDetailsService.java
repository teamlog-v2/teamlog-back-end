package com.test.teamlog.global.security;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.account.service.query.AccountQueryService;
import com.test.teamlog.global.security.UserAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 사용자에 관한 세부 정보 관리
 * UserDetailsService의 기본 구현체는 메모리에 자격 증명을 등록한다.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountQueryService accountQueryService;

    @Override
    public UserDetails loadUserByUsername(String identification) throws UsernameNotFoundException {
        // 여기서 UsernameNotFoundException은 RuntimeException인데 문서화를 위한 Exception이다.
        final User user = accountQueryService.findByIdentification(identification)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("(identification : %s) 유저를 찾을 수 없습니다", identification)));

        return new UserAdapter(user);
    }
}
