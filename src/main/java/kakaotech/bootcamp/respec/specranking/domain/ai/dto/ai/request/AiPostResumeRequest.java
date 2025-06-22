package kakaotech.bootcamp.respec.specranking.domain.ai.dto.ai.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AiPostResumeRequest(
        @JsonProperty("filelink")
        String resumeS3Url
) {
}
