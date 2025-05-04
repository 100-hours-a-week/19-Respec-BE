package kakaotech.bootcamp.respec.specranking.domain.spec.repository;

import java.util.List;
import java.util.Map;
import kakaotech.bootcamp.respec.specranking.domain.common.type.JobField;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.Spec;

public interface SpecRepositoryCustom {

    List<Spec> findByJobFieldWithPagination(JobField jobField, Long cursorId, int limit);

    int countByJobField(JobField jobField);

    Map<String, Integer> countByJobFields();

    int findRankByJobField(Long specId, JobField jobField);

    List<Spec> searchByNickname(String nickname, Long cursorId, int limit);

    Long findAbsoluteRank(JobField jobField, Long specId);

    long countDistinctUsersByJobField(JobField jobField);

    Double findAverageScoreByJobField(JobField jobField);
}
