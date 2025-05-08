package kakaotech.bootcamp.respec.specranking.domain.user.repository;

import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {
    long count();

    boolean existsByNickname(String nickname);

    boolean existsByLoginId(String loginId);
}
