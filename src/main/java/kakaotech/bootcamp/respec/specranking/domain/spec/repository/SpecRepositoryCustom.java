package kakaotech.bootcamp.respec.specranking.domain.spec.repository;

import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.common.type.JobField;
import kakaotech.bootcamp.respec.specranking.domain.spec.entity.Spec;

public interface SpecRepositoryCustom {

    List<Spec> findTopSpecsByJobFieldWithCursor(JobField jobField, Long cursorId, int limit);

    List<Spec> searchByNicknameWithCursor(String nickname, Long cursorId, int limit);

    Long countByJobField(JobField jobField);

    Long findAbsoluteRankByJobField(JobField jobField, Long specId);

    Double findAverageScoreByJobField(JobField jobField);
}
