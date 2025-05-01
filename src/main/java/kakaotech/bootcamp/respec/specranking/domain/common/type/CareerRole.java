package kakaotech.bootcamp.respec.specranking.domain.common.type;

import lombok.Getter;

@Getter
public enum CareerRole {
    CEO("CEO"),
    FULL_TIME_EMPLOYEE("정규직"),
    INTERN("인턴");

    private final String value;

    CareerRole(String value) {
        this.value = value;
    }
}
