package kakaotech.bootcamp.respec.specranking.domain.ai.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AiPostSpecResponse {

    private String nickname;

    @JsonProperty("academicScore")
    private Double academicScore = 0.0;

    @JsonProperty("workExperienceScore")
    private Double workExperienceScore = 0.0;

    @JsonProperty("certificationScore")
    private Double certificationScore = 0.0;

    @JsonProperty("languageProficiencyScore")
    private Double languageProficiencyScore = 0.0;

    @JsonProperty("extracurricularScore")
    private Double extracurricularScore = 0.0;

    @JsonProperty("totalScore")
    private Double totalScore = 0.0;
}