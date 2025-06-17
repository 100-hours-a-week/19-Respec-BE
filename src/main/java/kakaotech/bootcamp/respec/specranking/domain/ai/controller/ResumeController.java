package kakaotech.bootcamp.respec.specranking.domain.ai.controller;

import kakaotech.bootcamp.respec.specranking.domain.ai.dto.web.response.WebPostResumeResponse;
import kakaotech.bootcamp.respec.specranking.domain.ai.dto.web.response.WebPostResumeResponse.ResumeAnalysisResult;
import kakaotech.bootcamp.respec.specranking.domain.ai.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping("/resume/analysis")
    public WebPostResumeResponse analysisResume(
            @RequestParam MultipartFile resume
    ) {
        ResumeAnalysisResult resumeAnalysisResult = resumeService.analysisResume(resume);
        return new WebPostResumeResponse(true, "이력서 분석 성공", resumeAnalysisResult);
    }
}
