package kakaotech.bootcamp.respec.specranking.domain.common.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum CareerRole {
    CEO("CEO"),
    FULL_TIME_EMPLOYEE("정규직"),
    INTERN("인턴");

    private final String value;

    CareerRole(String value) {
        this.value = value;
    }
    
    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CareerRole fromValue(String value) {
        return Arrays.stream(CareerRole.values())
                .filter(role -> role.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown CareerRole value: " + value));
    }
}