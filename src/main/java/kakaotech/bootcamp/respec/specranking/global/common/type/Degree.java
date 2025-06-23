package kakaotech.bootcamp.respec.specranking.global.common.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Degree {
    DOCTORATE("박사"),
    MASTER("석사"),
    BACHELOR("학사"),
    ASSOCIATE("전문학사"),
    COMPLETION("수료");

    private final String value;

    Degree(String value) {
        this.value = value;
    }
    
    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Degree fromValue(String value) {
        return Arrays.stream(Degree.values())
                .filter(degree -> degree.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown Degree value: " + value));
    }
}
