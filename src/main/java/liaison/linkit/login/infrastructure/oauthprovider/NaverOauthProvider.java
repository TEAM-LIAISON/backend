//package liaison.linkit.login.infrastructure.oauthprovider;
//
//import liaison.linkit.global.exception.AuthException;
//import liaison.linkit.login.domain.OauthAccessToken;
//import liaison.linkit.login.domain.OauthProvider;
//import liaison.linkit.login.domain.OauthUserInfo;
//import liaison.linkit.login.infrastructure.oauthUserInfo.GoogleUserInfo;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Component;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//
//import java.util.Optional;
//
//import static liaison.linkit.global.exception.ExceptionCode.INVALID_AUTHORIZATION_CODE;
//import static liaison.linkit.global.exception.ExceptionCode.NOT_SUPPORTED_OAUTH_SERVICE;
//
//@Component
//public class NaverOauthProvider implements OauthProvider {
//    private static final String PROVIDER_NAME = "naver";
//
//    protected final String clientId;
//    protected final String clientSecret;
//    protected final String redirectUri;
//    protected final String tokenUri;
//    protected final String userUri;
//
//    public NaverOauthProvider (
//            @Value("${NAVER_CLIENT_ID}") final String clientId,
//            @Value("${NAVER_CLIENT_SECRET}") final String clientSecret,
//            @Value("${NAVER_REDIRECT_URL}") String redirectUri,
//            @Value("${NAVER_TOKEN_URI}") String tokenUri,
//            @Value("${NAVER_USER_INFO_URI}") String userUri
//    ) {
//        this.clientId = clientId;
//        this.clientSecret = clientSecret;
//        this.redirectUri = redirectUri;
//        this.tokenUri = tokenUri;
//        this.userUri = userUri;
//    }
//
//    @Override
//    public boolean is(final String name) { return PROVIDER_NAME.equals(name); }
//
//    @Override
//    public OauthUserInfo getUserInfo(final String code) {
//        final String accessToken = requestAccessToken(code);
//        final HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(accessToken);
//        final HttpEntity<MultiValueMap<String, String>> userInfoRequestEntity = new HttpEntity<>(headers);
//
//        final ResponseEntity<GoogleUserInfo> response = restTemplate.exchange(
//                userUri,
//                HttpMethod.GET,
//                userInfoRequestEntity,
//                GoogleUserInfo.class
//        );
//
//        if (response.getStatusCode().is2xxSuccessful()) {
//            return response.getBody();
//        }
//        throw new AuthException(NOT_SUPPORTED_OAUTH_SERVICE);
//    }
//
//    private String requestAccessToken(final String code) {
//        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        final HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.setBasicAuth(clientId, clientSecret);
//        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        params.add("code", code);
//        params.add("client_id", clientId);
//        params.add("client_secret", clientSecret);
//        params.add("redirect_uri", redirectUri);
//        params.add("grant_type", "authorization_code");
//
//        final HttpEntity<MultiValueMap<String, String>> accessTokenRequestEntity = new HttpEntity<>(params, httpHeaders);
//        final ResponseEntity<OauthAccessToken> accessTokenResponse = restTemplate.exchange(
//                tokenUri,
//                HttpMethod.POST,
//                accessTokenRequestEntity,
//                OauthAccessToken.class
//        );
//
//        return Optional.ofNullable(accessTokenResponse.getBody())
//                .orElseThrow(() -> new AuthException(INVALID_AUTHORIZATION_CODE))
//                .getAccessToken();
//    }
//}
