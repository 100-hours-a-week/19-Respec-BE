package kakaotech.bootcamp.respec.specranking.global.infrastructure.s3.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageFileStore {
    String upload(MultipartFile multipartFile);

    void delete(String fileName);

    String getDefaultImageUrl();
}
