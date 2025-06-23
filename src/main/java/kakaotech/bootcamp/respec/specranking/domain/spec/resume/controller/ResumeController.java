package kakaotech.bootcamp.respec.specranking.domain.spec.resume.controller;

import kakaotech.bootcamp.respec.specranking.domain.spec.resume.dto.response.WebPostResumeResponse;
import kakaotech.bootcamp.respec.specranking.domain.spec.resume.dto.response.WebPostResumeResponse.ResumeAnalysisResult;
import kakaotech.bootcamp.respec.specranking.domain.spec.resume.service.ResumeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping("/resume/analysis")
    public WebPostResumeResponse analysisResume(
            @RequestParam MultipartFile resume
    ) {
        log.info("Enter resume analysis");
        ResumeAnalysisResult resumeAnalysisResult = resumeService.analysisResume(resume);
        return new WebPostResumeResponse(true, "이력서 분석 성공", resumeAnalysisResult);
    }
}
