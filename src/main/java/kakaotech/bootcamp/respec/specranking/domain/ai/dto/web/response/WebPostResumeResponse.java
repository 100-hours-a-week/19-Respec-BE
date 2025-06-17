package kakaotech.bootcamp.respec.specranking.domain.ai.dto.web.response;

import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.common.type.Degree;
import kakaotech.bootcamp.respec.specranking.domain.common.type.FinalStatus;
import kakaotech.bootcamp.respec.specranking.domain.common.type.Institute;
import kakaotech.bootcamp.respec.specranking.domain.common.type.JobField;
import kakaotech.bootcamp.respec.specranking.domain.common.type.LanguageTest;
import kakaotech.bootcamp.respec.specranking.domain.common.type.Position;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class WebPostResumeResponse {
    private final Boolean isSuccess;
    private final String message;
    private final ResumeAnalysisResult data;

    @Getter
    @RequiredArgsConstructor
    public static class ResumeAnalysisResult {
        private final FinalEducation finalEducation;
        private final List<EducationDetails> educationDetails;
        private final List<WorkExperience> workExperiences;
        private final List<Certification> certifications;
        private final List<LanguageSkill> languageSkills;
        private final List<Activity> activities;
        private final JobField jobField;
    }

    @Getter
    @RequiredArgsConstructor
    public static class FinalEducation {
        private final Institute institute;
        private final FinalStatus finalStatus;
    }

    @Getter
    @RequiredArgsConstructor
    public static class EducationDetails {
        private final String schoolName;
        private final Degree degree;
        private final String major;
        private final Double gpa;
        private final Double maxGpa;
    }

    @Getter
    @RequiredArgsConstructor
    public static class WorkExperience {
        private final String company;
        private final Position position;
        private final Integer period;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Certification {
        private final String name;
    }

    @Getter
    @RequiredArgsConstructor
    public static class LanguageSkill {
        private final LanguageTest name;
        private final String score;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Activity {
        private final String name;
        private final String role;
        private final String award;
    }
}
