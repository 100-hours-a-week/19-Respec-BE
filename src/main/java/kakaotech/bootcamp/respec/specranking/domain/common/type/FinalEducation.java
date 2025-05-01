package kakaotech.bootcamp.respec.specranking.domain.common.type;

import lombok.Getter;

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
}
