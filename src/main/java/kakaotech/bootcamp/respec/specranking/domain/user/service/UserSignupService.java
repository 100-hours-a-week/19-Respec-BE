package kakaotech.bootcamp.respec.specranking.domain.user.service;

import jakarta.servlet.http.HttpServletResponse;
import kakaotech.bootcamp.respec.specranking.domain.auth.cookie.CookieUtils;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthTokenRequest;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthTokenResponse;
import kakaotech.bootcamp.respec.specranking.domain.auth.entity.OAuth;
import kakaotech.bootcamp.respec.specranking.domain.auth.repository.OAuthRepository;
import kakaotech.bootcamp.respec.specranking.domain.auth.service.AuthService;
import kakaotech.bootcamp.respec.specranking.domain.user.constants.AuthConstants;
import kakaotech.bootcamp.respec.specranking.domain.user.constants.UserMessages;
import kakaotech.bootcamp.respec.specranking.domain.user.dto.UserSignupRequest;
import kakaotech.bootcamp.respec.specranking.domain.user.dto.UserSignupResponse;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.validator.UserSignupValidator;
import kakaotech.bootcamp.respec.specranking.global.common.cookie.CookieConstants;
import kakaotech.bootcamp.respec.specranking.global.common.type.OAuthProvider;
import kakaotech.bootcamp.respec.specranking.global.exception.CustomException;
import kakaotech.bootcamp.respec.specranking.global.exception.ErrorCode;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.s3.service.ImageFileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserSignupService {

    private final UserRepository userRepository;
    private final OAuthRepository oAuthRepository;
    private final ImageFileStore imageFileStore;
    private final UserSignupValidator userSignupValidator;
    private final CookieUtils cookieUtils;
    private final AuthService authService;

    public UserSignupResponse signup(UserSignupRequest userSignupRequest, HttpServletResponse httpServletResponse) {
        userSignupValidator.validateSignupRequest(userSignupRequest);

        User user = createUser(userSignupRequest);
        createOAuthRecord(user, userSignupRequest.loginId());
        issueTokenAndSetCookies(user, httpServletResponse);

        return UserSignupResponse.success(user, UserMessages.SIGN_UP_SUCCESS);
    }

    private User createUser(UserSignupRequest request) {
        String profileImageUrl = (request.profileImageUrl() != null && !request.profileImageUrl().isEmpty())
                ? imageFileStore.upload(request.profileImageUrl())
                : imageFileStore.getDefaultImageUrl();

        User user = new User(
                request.loginId(),
                generateRandomPassword(),
                profileImageUrl,
                request.nickname(),
                true
        );

        return userRepository.save(user);
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, AuthConstants.RANDOM_PASSWORD_LENGTH);
    }

    private void createOAuthRecord(User user, String loginId) {
        UserSignupService.OAuthInfo oAuthInfo = UserSignupService.OAuthInfo.parse(loginId);

        OAuth oAuth = new OAuth(
                user,
                oAuthInfo.providerName(),
                oAuthInfo.providerId()
        );

        oAuthRepository.save(oAuth);
    }

    private void issueTokenAndSetCookies(User user, HttpServletResponse httpServletResponse) {
        cookieUtils.deleteCookie(httpServletResponse, CookieConstants.TEMP_LOGIN_ID);

        AuthTokenRequest tokenRequest = new AuthTokenRequest(user.getId(), user.getLoginId());
        AuthTokenResponse tokenResponse = authService.issueToken(tokenRequest, false);

        authService.setTokensInResponse(tokenResponse, httpServletResponse);

        cookieUtils.addCookie(
                httpServletResponse,
                CookieConstants.ACCESS_TOKEN,
                tokenResponse.accessToken(),
                (int) (CookieConstants.ACCESS_TOKEN_EXP / 1000)
        );
    }

    private record OAuthInfo (
            OAuthProvider providerName,
            String providerId
    ) {
        public static OAuthInfo parse(String loginId) {
            String[] parts = loginId.split(AuthConstants.LOGIN_ID_DELIMITER, AuthConstants.LOGIN_ID_PARTS_COUNT);

            if (parts.length != AuthConstants.LOGIN_ID_PARTS_COUNT) {
                throw new CustomException(ErrorCode.INVALID_LOGIN_ID_FORMAT);
            }

            OAuthProvider providerName = OAuthProvider.valueOf(parts[0].toLowerCase());
            String providerId = parts[1];

            return new OAuthInfo(providerName, providerId);
        }
    }
}
