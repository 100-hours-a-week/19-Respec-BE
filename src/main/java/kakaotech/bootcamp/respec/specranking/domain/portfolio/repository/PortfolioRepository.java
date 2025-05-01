package kakaotech.bootcamp.respec.specranking.domain.portfolio.repository;

import kakaotech.bootcamp.respec.specranking.domain.portfolio.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
}
