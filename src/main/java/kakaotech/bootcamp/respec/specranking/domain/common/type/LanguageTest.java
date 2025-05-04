package kakaotech.bootcamp.respec.specranking.domain.common.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum LanguageTest {
    TOEIC_ENGLISH("TOEIC_ENGLISH"),
    TOEFL_ENGLISH("TOEFL_ENGLISH"),
    TEPS_ENGLISH("TEPS_ENGLISH"),
    G_TELP_ENGLISH("G_TELP_ENGLISH"),
    TOEIC_SPEAKING_ENGLISH("TOEIC_SPEAKING_ENGLISH"),
    TEPS_SPEAKING_ENGLISH("TEPS_SPEAKING_ENGLISH"),
    G_TELP_SPEAKING_ENGLISH("G_TELP_SPEAKING_ENGLISH"),
    IELTS_ENGLISH("IELTS_ENGLISH"),
    SNULT_GERMAN("SNULT_GERMAN"),
    SNULT_FRENCH("SNULT_FRENCH"),
    SNULT_RUSSIAN("SNULT_RUSSIAN"),
    SNULT_CHINESE("SNULT_CHINESE"),
    SNULT_JAPANESE("SNULT_JAPANESE"),
    SNULT_SPANISH("SNULT_SPANISH"),
    NEW_HSK_CHINESE("NEW_HSK_CHINESE"),
    JPT_JAPANESE("JPT_JAPANESE"),
    FLEX_ENGLISH("FLEX_ENGLISH"),
    FLEX_GERMAN("FLEX_GERMAN"),
    FLEX_FRENCH("FLEX_FRENCH"),
    FLEX_SPANISH("FLEX_SPANISH"),
    FLEX_RUSSIAN("FLEX_RUSSIAN"),
    FLEX_JAPANESE("FLEX_JAPANESE"),
    FLEX_CHINESE("FLEX_CHINESE"),
    OPIC_ENGLISH("OPIC_ENGLISH"),
    OPIC_CHINESE("OPIC_CHINESE"),
    OPIC_RUSSIAN("OPIC_RUSSIAN"),
    OPIC_SPANISH("OPIC_SPANISH"),
    OPIC_JAPANESE("OPIC_JAPANESE"),
    OPIC_VIETNAMESE("OPIC_VIETNAMESE");


    private final String value;

    LanguageTest(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static LanguageTest fromValue(String value) {
        return Arrays.stream(LanguageTest.values())
                .filter(test -> test.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown LanguageTest value: " + value));
    }
}
