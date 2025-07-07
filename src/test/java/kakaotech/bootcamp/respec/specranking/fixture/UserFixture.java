package kakaotech.bootcamp.respec.specranking.fixture;

import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.util.UserUtils;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Optional;

import static kakaotech.bootcamp.respec.specranking.fixture.TestConstants.*;
import static org.mockito.Mockito.lenient;

public class UserFixture {

    public static User createMockUser() {
        return createMockUser(DEFAULT_USER_ID, DEFAULT_USER_NICKNAME, DEFAULT_USER_PROFILE_URL);
    }

    public static User createMockUser(Long userId, String nickname, String profileUrl) {
        User user = Mockito.mock(User.class);
        lenient().when(user.getId()).thenReturn(userId);
        lenient().when(user.getNickname()).thenReturn(nickname);
        lenient().when(user.getUserProfileUrl()).thenReturn(profileUrl);
        return user;
    }

    public static void setupAuthenticatedUser(MockedStatic<UserUtils> mockUserUtils) {
        setupAuthenticatedUser(mockUserUtils, DEFAULT_USER_ID);
    }

    public static void setupAuthenticatedUser(MockedStatic<UserUtils> mockUserUtils, Long userId) {
        mockUserUtils.when(UserUtils::getCurrentUserId).thenReturn(Optional.of(userId));
    }

    public static void setupUnauthenticatedUser(MockedStatic<UserUtils> mockUserUtils) {
        mockUserUtils.when(UserUtils::getCurrentUserId).thenReturn(Optional.empty());
    }

    private UserFixture() { }
}
