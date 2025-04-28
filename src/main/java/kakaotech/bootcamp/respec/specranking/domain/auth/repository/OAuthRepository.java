package kakaotech.bootcamp.respec.specranking.domain.auth.repository;

import kakaotech.bootcamp.respec.specranking.domain.auth.entity.OAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuthRepository extends JpaRepository<OAuth, Long> {
}
