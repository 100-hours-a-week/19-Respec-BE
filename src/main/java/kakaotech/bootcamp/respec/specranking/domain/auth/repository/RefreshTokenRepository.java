package kakaotech.bootcamp.respec.specranking.domain.auth.repository;

import kakaotech.bootcamp.respec.specranking.domain.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}
