package kakaotech.bootcamp.respec.specranking.domain.spec.spec.sub.activitynetworking.repository;

import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.sub.activitynetworking.entity.ActivityNetworking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityNetworkingRepository extends JpaRepository<ActivityNetworking, Long> {
    List<ActivityNetworking> findBySpecId(Long specId);
}
