package kakaotech.bootcamp.respec.specranking.domain.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static kakaotech.bootcamp.respec.specranking.global.common.type.OAuthProvider.kakao;

@Getter
@RequiredArgsConstructor
public class KakaoResponse implements OAuth2Response{

    private final Map<String, Object> attribute;

    @Override
    public String getProvider() {
        return kakao.name();
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }
}
