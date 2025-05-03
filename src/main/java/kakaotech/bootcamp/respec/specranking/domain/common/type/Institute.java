package kakaotech.bootcamp.respec.specranking.domain.common.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum FinalEducation {
    MIDDLE_SCHOOL("중학교"),
    HIGH_SCHOOL("고등학교"),
    TWO_THREE_YEAR_COLLEGE("2/3년 대학교"),
    UNIVERSITY("대학교"),
    GRADUATE_SCHOOL("대학원");

    private final String value;

    FinalEducation(String value) {
        this.value = value;
    }
    
    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static FinalEducation fromValue(String value) {
        return Arrays.stream(FinalEducation.values())
                .filter(status -> status.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown FinalEducation value: " + value));
    }
}