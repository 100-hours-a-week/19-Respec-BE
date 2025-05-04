package kakaotech.bootcamp.respec.specranking.domain.activitynetworking.repository;

import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.activitynetworking.entity.ActivityNetworking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityNetworkingRepository extends JpaRepository<ActivityNetworking, Long> {
    List<ActivityNetworking> findBySpecId(Long specId);
}
