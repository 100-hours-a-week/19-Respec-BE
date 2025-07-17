package kakaotech.bootcamp.respec.specranking.domain.spec.spec.service.query;

import static kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.QSpec.spec;
import static kakaotech.bootcamp.respec.specranking.global.common.util.cursor.CursorUtils.processCursorPagination;

import com.querydsl.core.Tuple;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.entity.Spec;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.repository.SpecRepository;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import kakaotech.bootcamp.respec.specranking.global.common.type.JobField;
import kakaotech.bootcamp.respec.specranking.global.common.util.cursor.CursorPagination;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SpecRankingsQueryService {

    private final SpecRepository specRepository;
    private final UserRepository userRepository;

    public RankingsBundle fetchRankingsBundle(JobField jobField, Long cursorId, int limit) {
        List<Spec> specs = specRepository.findTopSpecsByJobFieldWithCursor(jobField, cursorId, limit + 1);

        CursorPagination<Spec> cursorPagination = processCursorPagination(specs, limit, Spec::getId);
        boolean hasNext = cursorPagination.hasNext();
        specs = cursorPagination.items();
        String nextCursor = cursorPagination.nextCursor();

        long countUsersHavingSpec = userRepository.countUsersHavingSpec();
        Map<JobField, Long> jobFieldCountMap = getJobFieldCountMap(specs);

        return new RankingsBundle(specs, hasNext, nextCursor, countUsersHavingSpec, jobFieldCountMap);
    }

    private Map<JobField, Long> getJobFieldCountMap(List<Spec> specs) {
        List<JobField> jobFields = specs.stream()
                .map(Spec::getJobField)
                .distinct()
                .toList();

        List<Tuple> tuples = specRepository.countByJobFields(jobFields);
        return tuples.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(spec.jobField),
                        tuple -> tuple.get(spec.count())
                ));
    }

    public record RankingsBundle(
            List<Spec> specs,
            boolean hasNext,
            String nextCursor,
            long totalUsersCount,
            Map<JobField, Long> jobFieldCountMap
    ) {
    }
}
