package kakaotech.bootcamp.respec.specranking.domain.store.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Profile("!portfolio-s3")
public class FileMemoryStore implements FileStore {
    private final List<String> uploadedFiles = new ArrayList<>();

    @Override
    public String upload(MultipartFile multipartFile) {
        validateExistsMultipartFile(multipartFile);

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);

        uploadedFiles.add(storeFileName);
        return "mock:url:portfolio:example.com/" + storeFileName;
    }

    @Override
    public void delete(String fileId) {
        uploadedFiles.remove(fileId);
    }

    public boolean exists(String fileId) {
        return uploadedFiles.contains(fileId);
    }

    public int count() {
        return uploadedFiles.size();
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