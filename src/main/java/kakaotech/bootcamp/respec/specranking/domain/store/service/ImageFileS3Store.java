package kakaotech.bootcamp.respec.specranking.domain.store.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import kakaotech.bootcamp.respec.specranking.domain.store.config.S3DefaultImageConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
@Profile("imageS3")
public class ImageFileS3Store implements ImageFileStore {

    private final AmazonS3Client amazonS3Client;
    private final S3DefaultImageConfig defaultImageConfig;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 허용된 이미지 타입
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png"
    );

    // 최대 파일 크기 (10MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    @Override
    public String upload(MultipartFile multipartFile) {
        validateFile(multipartFile);

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);

        try {
            ObjectMetadata metadata = createS3MetaData(multipartFile);
            amazonS3Client.putObject(bucket, storeFileName, multipartFile.getInputStream(), metadata);
        } catch (IOException e) {
            throw new IllegalArgumentException("S3 업로드 실패", e);
        }

        return amazonS3Client.getUrl(bucket, storeFileName).toString();
    }

    @Override
    public void delete(String fileName) {
        // 기본 이미지는 삭제하지 않음
        if (fileName.equals(getDefaultImageUrl())) {
            return;
        }

        try {
            // URL에서 파일명 추출
            String key = extractKeyFromUrl(fileName);
            amazonS3Client.deleteObject(bucket, key);
        } catch (Exception e) {
            throw new IllegalArgumentException("S3 파일 삭제 실패", e);
        }
    }

    @Override
    public String getDefaultImageUrl() {
        // 기본 이미지 설정에서 URL 가져오기
        return defaultImageConfig.getDefaultImageUrl();
    }

    private String extractKeyFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    private static ObjectMetadata createS3MetaData(MultipartFile multipartFile) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());
        return metadata;
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

        // 파일 타입 검증
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("PNG 또는 JPG 형식의 이미지만 업로드 가능합니다.");
        }

        // 파일 크기 검증 (10MB)
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("이미지 크기는 10MB 이하여야 합니다.");
        }
    }
}
