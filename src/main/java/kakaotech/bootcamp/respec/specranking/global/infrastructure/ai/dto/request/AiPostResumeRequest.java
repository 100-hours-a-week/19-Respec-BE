package kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AiPostResumeRequest(
        @JsonProperty("filelink")
        String resumeS3Url
) {
}
