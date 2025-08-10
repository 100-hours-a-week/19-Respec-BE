package kakaotech.bootcamp.respec.specranking.global.infrastructure.s3.service;

import static kakaotech.bootcamp.respec.specranking.global.infrastructure.s3.constant.S3Constant.S3_FILE_REMOVE_FAIL_MESSAGE;
import static kakaotech.bootcamp.respec.specranking.global.infrastructure.s3.constant.S3Constant.S3_UPLOAD_FAIL_MESSAGE;
import static kakaotech.bootcamp.respec.specranking.global.infrastructure.s3.util.FileStoreUtil.createStoreFileName;
import static kakaotech.bootcamp.respec.specranking.global.infrastructure.s3.util.FileStoreUtil.validateExistsMultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.IOException;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.s3.exception.S3RemoveFileFailException;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.s3.exception.S3UploadFailException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
@Profile("resumeS3")
public class ResumeS3Store implements ResumeStore {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.resume.bucket}")
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
            throw new S3UploadFailException(S3_UPLOAD_FAIL_MESSAGE, e);
        }

        return amazonS3Client.getUrl(bucket, storeFileName).toString();
    }

    @Override
    public void delete(String fileName) {
        try {
            amazonS3Client.deleteObject(bucket, fileName);
        } catch (Exception e) {
            throw new S3RemoveFileFailException(S3_FILE_REMOVE_FAIL_MESSAGE, e);
        }
    }

    private static ObjectMetadata createS3MetaData(MultipartFile multipartFile) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());
        return metadata;
    }
}
