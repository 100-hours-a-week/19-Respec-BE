package kakaotech.bootcamp.respec.specranking.domain.store.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageFileStore {
    String upload(MultipartFile multipartFile);

    void delete(String fileName);

    String getDefaultImageUrl();
}
