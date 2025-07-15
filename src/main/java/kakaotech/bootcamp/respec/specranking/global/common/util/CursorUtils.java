package kakaotech.bootcamp.respec.specranking.global.common.util;

import java.util.Base64;

public class CursorUtils {
    public static String encodeCursor(Long id) {
        return Base64.getEncoder().encodeToString(String.valueOf(id).getBytes());
    }

    public static Long decodeCursor(String cursor) {
        if (cursor == null || cursor.isEmpty()) {
            return Long.MAX_VALUE;
        }

        byte[] decodedBytes = Base64.getDecoder().decode(cursor);
        String decodedString = new String(decodedBytes);
        return Long.parseLong(decodedString);
    }

    public static boolean isFirstCursor(Long cursorId) {
        return cursorId == null || cursorId == Long.MAX_VALUE;
    }
}
