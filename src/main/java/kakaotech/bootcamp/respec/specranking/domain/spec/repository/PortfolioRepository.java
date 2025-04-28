package kakaotech.bootcamp.respec.specranking.domain.spec.repository;

import kakaotech.bootcamp.respec.specranking.domain.spec.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
}
