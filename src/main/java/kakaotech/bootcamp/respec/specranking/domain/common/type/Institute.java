package kakaotech.bootcamp.respec.specranking.domain.common.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum Institute {
    MIDDLE_SCHOOL("중학교"),
    HIGH_SCHOOL("고등학교"),
    TWO_THREE_YEAR_COLLEGE("2_3년 대학교"),
    UNIVERSITY("대학교"),
    GRADUATE_SCHOOL("대학원");

    private final String value;

    Institute(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Institute fromValue(String value) {
        return Arrays.stream(Institute.values())
                .filter(status -> status.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown FinalEducation value: " + value));
    }
}