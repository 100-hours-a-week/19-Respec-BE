package kakaotech.bootcamp.respec.specranking.domain.common.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum FinalStatus {
    GRADUATED("졸업"),
    COMPLETED("수료"),
    DROPPED_OUT("중퇴"),
    ABSENCE("휴학"),
    ENROLLED("재학");

    private final String value;

    FinalStatus(String value) {
        this.value = value;
    }
    
    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static FinalStatus fromValue(String value) {
        return Arrays.stream(FinalStatus.values())
                .filter(status -> status.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown FinalStatus value: " + value));
    }
}