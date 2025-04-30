package kakaotech.bootcamp.respec.specranking.domain.store;

import com.amazonaws.services.s3.AmazonS3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Profile("!dev")
public class FileS3Store implements FileStore{

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public String upload(MultipartFile multipartFile) {
        validateExistsMultipartFile(multipartFile);

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
        try {
            amazonS3Client.deleteObject(bucket, fileName);
        } catch (Exception e) {
            throw new IllegalArgumentException("S3 파일 삭제 실패", e);
        }
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

    private void validateExistsMultipartFile(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("파일이 존재하지 않습니다.");
        }
    }
}
