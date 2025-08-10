package kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AiPostSpecResponse {

    private String nickname;

    @JsonProperty("academicScore")
    private Double educationScore = 0.0;

    @JsonProperty("workExperienceScore")
    private Double workExperienceScore = 0.0;

    @JsonProperty("certificationScore")
    private Double certificationScore = 0.0;

    @JsonProperty("languageProficiencyScore")
    private Double languageSkillScore = 0.0;

    @JsonProperty("extracurricularScore")
    private Double activityNetworkingScore = 0.0;

    @JsonProperty("totalScore")
    private Double totalScore = 0.0;

    @JsonProperty("assessment")
    private String assessment = "";
}
