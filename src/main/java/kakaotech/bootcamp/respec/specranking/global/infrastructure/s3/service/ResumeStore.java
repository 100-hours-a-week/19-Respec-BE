package kakaotech.bootcamp.respec.specranking.global.infrastructure.s3.service;

import org.springframework.web.multipart.MultipartFile;

public interface ResumeStore {
    String upload(MultipartFile multipartFile);

    void delete(String fileName);
}
