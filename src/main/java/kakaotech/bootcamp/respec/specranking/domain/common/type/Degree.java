package kakaotech.bootcamp.respec.specranking.domain.common.type;

import lombok.Getter;

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
}
