package liaison.linkit.login.domain;

import org.springframework.web.client.RestTemplate;

public interface OauthProvider {
    RestTemplate restTemplate = new RestTemplate();

    boolean is(String name);

    OauthUserInfo getUserInfo(String code);
}
