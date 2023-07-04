package com.test.teamlog.service;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.account.repository.UserRepository;
import com.test.teamlog.global.security.UserAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserAdapter loadUserByUsername(String identification) throws UsernameNotFoundException {
        final User user = userRepository.findByIdentification(identification)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("(identification : %s) 유저를 찾을 수 없습니다", identification)));

        return new UserAdapter(user);
    }
}
