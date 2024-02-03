package com.test.teamlog.global.auth;

import com.test.teamlog.domain.account.model.AuthType;
import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.account.service.command.AccountCommandService;
import com.test.teamlog.domain.account.service.query.AccountQueryService;
import com.test.teamlog.domain.file.info.entity.FileInfo;
import com.test.teamlog.global.security.UserAdapter;
import com.test.teamlog.global.utility.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final AccountCommandService accountCommandService;
    private final AccountQueryService accountQueryService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        final OAuth2User oAuth2User = super.loadUser(userRequest);
        final CompactOAuth2User compactOAuth2User
                = CompactOAuthUserMapper.convert(oAuth2User, userRequest.getClientRegistration().getRegistrationId());

        final String identification = compactOAuth2User.getIdentification();
        final Optional<User> user = accountQueryService.findByIdentification(identification);
        if (user.isPresent()) {
            return new UserAdapter(user.get(), oAuth2User.getAttributes());
        }

        FileInfo profileImage = null;
        if (StringUtils.hasText(compactOAuth2User.getProfileImgUrl())) {
            profileImage = FileInfo.builder().storedFilePath(compactOAuth2User.getProfileImgUrl()).build();
        }

        return new UserAdapter(
                accountCommandService.save(User.builder()
                        .identification(identification)
                        .password(PasswordUtil.encode(UUID.randomUUID().toString()))
                        .name(identification)
                        .profileImage(profileImage)
                        .authType(AuthType.fromName(compactOAuth2User.getAuthType()))
                        .build()),
                oAuth2User.getAttributes());
    }
}
