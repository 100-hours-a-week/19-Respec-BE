package kakaotech.bootcamp.respec.specranking.global.common.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum Position {
    CEO("대표"),
    FULL_TIME_EMPLOYEE("정규직"),
    INTERN("인턴");

    private final String value;

    Position(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Position fromValue(String value) {
        return Arrays.stream(Position.values())
                .filter(role -> role.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown CareerRole value: " + value));
    }
}
