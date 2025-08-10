package kakaotech.bootcamp.respec.specranking.domain.user.validator;

import kakaotech.bootcamp.respec.specranking.domain.user.constants.FileConstants;
import kakaotech.bootcamp.respec.specranking.global.exception.CustomException;
import kakaotech.bootcamp.respec.specranking.global.exception.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class UserCommonValidator {

    public void validateProfileImageFile(MultipartFile profileImageUrl) {
        if (profileImageUrl == null || profileImageUrl.isEmpty()) return;

        validateFileContentType(profileImageUrl);
        validateFileSize(profileImageUrl);
    }

    private void validateFileContentType(MultipartFile profileImageUrl) {
        String contentType = profileImageUrl.getContentType();
        if (contentType == null || !FileConstants.ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new CustomException(ErrorCode.INVALID_FILE_FORMAT);
        }
    }

    private void validateFileSize(MultipartFile profileImageUrl) {
        if (profileImageUrl.getSize() > FileConstants.MAX_FILE_SIZE) {
            throw new CustomException(ErrorCode.FILE_SIZE_EXCEEDED);
        }
    }
}
