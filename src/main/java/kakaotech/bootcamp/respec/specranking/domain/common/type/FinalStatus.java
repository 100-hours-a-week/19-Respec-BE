package kakaotech.bootcamp.respec.specranking.domain.common.type;

import lombok.Getter;

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
}
