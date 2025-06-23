package kakaotech.bootcamp.respec.specranking.domain.auth.repository;

import kakaotech.bootcamp.respec.specranking.domain.auth.entity.OAuth;
import kakaotech.bootcamp.respec.specranking.global.common.type.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthRepository extends JpaRepository<OAuth, Long> {
    Optional<OAuth> findByProviderNameAndProviderId(OAuthProvider providerName, String providerId);
}
