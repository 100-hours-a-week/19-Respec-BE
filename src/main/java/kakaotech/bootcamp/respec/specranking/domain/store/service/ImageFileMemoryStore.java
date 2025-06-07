package kakaotech.bootcamp.respec.specranking.domain.store.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
@Profile("!s3")
public class ImageFileMemoryStore implements ImageFileStore {

    private final List<String> uploadedFiles = new ArrayList<>();

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png"
    );
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final String DEFAULT_IMAGE_URL = "mock:url:image:example.com/default-profile.png";

    @Override
    public String upload(MultipartFile multipartFile) {
        validateFile(multipartFile);

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);

        uploadedFiles.add(storeFileName);
        return "mock:url:image:example.com/" + storeFileName;
    }

    @Override
    public void delete(String fileName) {
        if (fileName.equals(getDefaultImageUrl())) {
            return;
        }

        String key = extractKeyFromUrl(fileName);
        uploadedFiles.remove(key);
    }

    @Override
    public String getDefaultImageUrl() {
        return DEFAULT_IMAGE_URL;
    }

    private String extractKeyFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 존재하지 않습니다.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("PNG 또는 JPG 형식의 이미지만 업로드 가능합니다.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("이미지 크기는 10MB 이하여야 합니다.");
        }
    }
}
