package kakaotech.bootcamp.respec.specranking.global.infrastructure.s3.service;

import static kakaotech.bootcamp.respec.specranking.global.infrastructure.s3.constant.S3Constant.MOCK_FILE_BASE_URL;
import static kakaotech.bootcamp.respec.specranking.global.infrastructure.s3.util.FileStoreUtil.createStoreFileName;
import static kakaotech.bootcamp.respec.specranking.global.infrastructure.s3.util.FileStoreUtil.validateExistsMultipartFile;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Profile("!resumeS3")
public class ResumeMemoryStore implements ResumeStore {
    private final List<String> uploadedFiles = new ArrayList<>();

    @Override
    public String upload(MultipartFile multipartFile) {
        validateExistsMultipartFile(multipartFile);

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);
        uploadedFiles.add(storeFileName);
        return MOCK_FILE_BASE_URL + "/" + storeFileName;
    }

    @Override
    public void delete(String fileId) {
        uploadedFiles.remove(fileId);
    }

    public int count() {
        return uploadedFiles.size();
    }
}
