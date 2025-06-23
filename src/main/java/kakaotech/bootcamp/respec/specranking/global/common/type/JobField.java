package kakaotech.bootcamp.respec.specranking.global.common.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum JobField {
    TOTAL("전체"),
    MANAGEMENT_BUSINESS("경영_사무"),
    MARKETING_ADVERTISING_PR("마케팅_광고_홍보"),
    TRADE_LOGISTICS("무역_유통"),
    INTERNET_IT("인터넷_IT"),
    PRODUCTION_MANUFACTURING("생산_제조"),
    SALES_CUSTOMER_SERVICE("영업_고객상담"),
    CONSTRUCTION("건설"),
    FINANCE("금융"),
    RND_PLANNING("연구개발_설계"),
    DESIGN("디자인"),
    MEDIA("미디어"),
    SPECIALIZED_TECHNICAL("전문직_특수직");

    private final String value;

    JobField(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static JobField fromValue(String value) {
        return Arrays.stream(JobField.values())
                .filter(field -> field.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown JobField value: " + value));
    }
}
