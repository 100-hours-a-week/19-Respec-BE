package kakaotech.bootcamp.respec.specranking.domain.ai.dto.ai.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AiPostResumeRequest {
    @JsonProperty("fileUrl")
    private final String resumeS3Url;
}
