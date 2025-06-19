package kakaotech.bootcamp.respec.specranking.domain.ai.service;

import kakaotech.bootcamp.respec.specranking.domain.ai.dto.ai.mapping.AiDtoMapping;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.ai.request.AiPostResumeRequest;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.ai.response.AiPostResumeResponse;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.web.response.WebPostResumeResponse.ResumeAnalysisResult;
import kakaotech.bootcamp.respec.specranking.domain.store.service.ResumeStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeStore resumeStore;
    private final AiService aiService;

    public ResumeAnalysisResult analysisResume(MultipartFile resume) {
        if (!existsFile(resume)) {
            throw new IllegalArgumentException("resume not found");
        }

        String resumeUrl = resumeStore.upload(resume);

        AiPostResumeRequest aiPostResumeRequest = new AiPostResumeRequest(resumeUrl);
        AiPostResumeResponse aiPostResumeResponse = aiService.analyzeResume(aiPostResumeRequest);
        ResumeAnalysisResult resumeAnalysisResult = AiDtoMapping.convertToResumeAnalysisResponse(aiPostResumeResponse);

        return resumeAnalysisResult;
    }

    private static boolean existsFile(MultipartFile portfolioFile) {
        return portfolioFile != null;
    }
}
