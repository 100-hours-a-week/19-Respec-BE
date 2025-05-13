package kakaotech.bootcamp.respec.specranking.domain.store.config;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@RequiredArgsConstructor
@Profile("s3")
public class S3DefaultImageConfig {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final String DEFAULT_IMAGE_KEY = "specranking_default_image.png";

    private String defaultImageUrl;

    @PostConstruct
    public void init() {
        uploadDefaultImageIfNeeded();
        // S3 URL 미리 생성해두기
        defaultImageUrl = amazonS3Client.getUrl(bucket, DEFAULT_IMAGE_KEY).toString();
    }

    private void uploadDefaultImageIfNeeded() {
        try {
            // S3에 기본 이미지가 존재하는지 확인
            if (!amazonS3Client.doesObjectExist(bucket, DEFAULT_IMAGE_KEY)) {
                // resources 폴더에 저장된 기본 이미지 로드
                ClassPathResource resource = new ClassPathResource("static/images/specranking_default_image.png");

                try (InputStream inputStream = resource.getInputStream()) {
                    // 이미지 메타데이터 설정
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setContentLength(resource.contentLength());
                    metadata.setContentType("image/png");

                    // S3에 업로드
                    amazonS3Client.putObject(bucket, DEFAULT_IMAGE_KEY, inputStream, metadata);

                    System.out.println("기본 프로필 이미지가 S3에 업로드되었습니다.");
                }
            }
        } catch (IOException e) {
            System.err.println("기본 프로필 이미지 초기화 중 오류 발생: " + e.getMessage());
        }
    }

    public String getDefaultImageUrl() {
        return defaultImageUrl;
    }
}
