package kakaotech.bootcamp.respec.specranking.domain.spec.resume.service;

import kakaotech.bootcamp.respec.specranking.domain.spec.resume.dto.response.WebPostResumeResponse.ResumeAnalysisResult;
import kakaotech.bootcamp.respec.specranking.domain.spec.spec.dto.mapping.AiDtoMapping;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.dto.request.AiPostResumeRequest;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.dto.response.AiPostResumeResponse;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.ai.service.AiService;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.s3.service.ResumeStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeService {

    private final ResumeStore resumeStore;
    private final AiService aiService;

    public ResumeAnalysisResult analysisResume(MultipartFile resume) {
        if (!existsFile(resume)) {
            throw new IllegalArgumentException("resume not found");
        }
        log.info("Enter analysis resume and isExists");
        String resumeUrl = resumeStore.upload(resume);
        log.info("upload completed");
        AiPostResumeRequest aiPostResumeRequest = new AiPostResumeRequest(resumeUrl);
        log.info("ai resume started");
        AiPostResumeResponse aiPostResumeResponse = aiService.analyzeResume(aiPostResumeRequest);
        log.info("ai resume finished");
        ResumeAnalysisResult resumeAnalysisResult = AiDtoMapping.convertToResumeAnalysisResponse(aiPostResumeResponse);
        log.info("resume analysis finished");
        return resumeAnalysisResult;
    }

    private static boolean existsFile(MultipartFile portfolioFile) {
        return portfolioFile != null;
    }
}
