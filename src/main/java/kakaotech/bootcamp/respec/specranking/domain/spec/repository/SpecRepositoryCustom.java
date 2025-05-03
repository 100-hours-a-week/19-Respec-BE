package kakaotech.bootcamp.respec.specranking.domain.spec.repository;

import kakaotech.bootcamp.respec.specranking.domain.common.type.JobField;

import java.util.List;
import java.util.Map;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.Spec;

public interface SpecRepositoryCustom {

    List<Spec> findByJobFieldWithPagination(JobField jobField, Long cursorId, int limit);

    int countByJobField(String jobField);

    Map<String, Integer> countByJobFields();

    int findRankByJobField(Long specId, String jobField);

    List<Spec> searchByNickname(String nickname, Long cursorId, int limit);

    int findAbsoluteRank(String jobField, Long specId);
}
