package liaison.linkit.admin.service;

import liaison.linkit.admin.domain.AdminMember;
import liaison.linkit.admin.domain.repository.AdminMemberRepository;
import liaison.linkit.admin.dto.request.AdminLoginRequest;
import liaison.linkit.admin.infrastructure.PasswordEncoder;
import liaison.linkit.global.exception.AdminException;
import liaison.linkit.global.exception.AuthException;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.login.domain.RefreshToken;
import liaison.linkit.login.domain.repository.RefreshTokenRepository;
import liaison.linkit.login.infrastructure.BearerAuthorizationExtractor;
import liaison.linkit.login.infrastructure.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminLoginService {
    private final AdminMemberRepository adminMemberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final BearerAuthorizationExtractor bearerAuthorizationExtractor;
    private final PasswordEncoder passwordEncoder;

    public MemberTokens login(final AdminLoginRequest adminLoginRequest) {
        final AdminMember adminMember = adminMemberRepository
                .findByUsername(adminLoginRequest.getUsername())
                .orElseThrow(() -> new AdminException(INVALID_USER_NAME));

        if (passwordEncoder.matches(adminLoginRequest.getPassword(), adminMember.getPassword())) {
            final MemberTokens memberTokens = jwtProvider.generateLoginToken(adminMember.getId().toString());
            final RefreshToken savedRefreshToken = new RefreshToken(memberTokens.getRefreshToken(),
                    adminMember.getId());
            refreshTokenRepository.save(savedRefreshToken);
            return memberTokens;
        }

        throw new AdminException(INVALID_PASSWORD);
    }

    public String renewalAccessToken(final String refreshTokenRequest, final String authorizationHeader) {
        final String accessToken = bearerAuthorizationExtractor.extractAccessToken(authorizationHeader);
        if (jwtProvider.isValidRefreshAndInvalidAccess(refreshTokenRequest, accessToken)) {
            final RefreshToken refreshToken = refreshTokenRepository.findById(refreshTokenRequest)
                    .orElseThrow(() -> new AuthException(INVALID_REFRESH_TOKEN));
            return jwtProvider.regenerateAccessToken((refreshToken.getMemberId().toString()));
        }
        if (jwtProvider.isValidRefreshAndValidAccess(refreshTokenRequest, accessToken)) {
            return accessToken;
        }
        throw new AuthException(FAIL_TO_VALIDATE_TOKEN);
    }

    public void removeRefreshToken(final String refreshToken) {
        refreshTokenRepository.deleteById(refreshToken);
    }
}
