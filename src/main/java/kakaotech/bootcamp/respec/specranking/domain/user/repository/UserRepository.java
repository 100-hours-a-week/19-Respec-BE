package kakaotech.bootcamp.respec.specranking.domain.user.repository;

import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    long count();

    boolean existsByNickname(String nickname);

    boolean existsByNicknameAndIdNot(String nickname, Long userId);

    boolean existsByLoginId(String loginId);

    Optional<User> findByLoginId(String loginId);
}
