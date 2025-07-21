package kakaotech.bootcamp.respec.specranking.domain.auth.repository;

import kakaotech.bootcamp.respec.specranking.domain.auth.entity.RefreshToken;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Boolean existsByValue(String value);

    int deleteAllByExpirationBefore(LocalDateTime expiration);

    void deleteAllByUser(User user);
}
