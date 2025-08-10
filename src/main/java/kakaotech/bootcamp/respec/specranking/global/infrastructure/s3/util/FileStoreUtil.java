package kakaotech.bootcamp.respec.specranking.global.infrastructure.s3.util;

import static kakaotech.bootcamp.respec.specranking.global.infrastructure.s3.constant.S3Constant.FILE_NOT_FOUND_EXCEPTION_MESSAGE;

import java.util.UUID;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.s3.exception.FileNotFoundException;
import org.springframework.web.multipart.MultipartFile;

public class FileStoreUtil {

    public static String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private static String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    public static void validateExistsMultipartFile(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new FileNotFoundException(FILE_NOT_FOUND_EXCEPTION_MESSAGE);
        }
    }

}
