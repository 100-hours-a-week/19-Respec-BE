package kakaotech.bootcamp.respec.specranking.domain.common.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum ScoreCategoryDetail {

    EDUCATION_SCORE("학력_성적"),
    WORK_EXPERIENCE("직무_경험"),
    CERTIFICATION_SKILLS("자격증_스킬"),
    LANGUAGE_PROFICIENCY("어학_능력"),
    ACTIVITY_NETWORKING("활동_네트워킹");

    private final String value;

    ScoreCategoryDetail(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ScoreCategoryDetail fromValue(String value) {
        return Arrays.stream(ScoreCategoryDetail.values())
                .filter(scoreCategoryDetail -> scoreCategoryDetail.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown Category value: " + value));
    }
}