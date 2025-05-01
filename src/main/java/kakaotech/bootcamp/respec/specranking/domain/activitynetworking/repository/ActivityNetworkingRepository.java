package kakaotech.bootcamp.respec.specranking.domain.activitynetworking.repository;

import kakaotech.bootcamp.respec.specranking.domain.activitynetworking.entity.ActivityNetworking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityNetworkingRepository extends JpaRepository<ActivityNetworking, Long> {
    void deleteBySpecId(Long specId);
}
