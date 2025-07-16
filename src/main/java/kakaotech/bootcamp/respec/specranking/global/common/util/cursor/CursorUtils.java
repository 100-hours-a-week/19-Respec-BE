package kakaotech.bootcamp.respec.specranking.global.common.util.cursor;

import java.util.Base64;
import java.util.List;
import java.util.function.Function;

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

    public static <T> CursorPagination<T> processCursorPagination(List<T> items, int limit,
                                                                  Function<T, Long> idExtractor) {
        boolean hasNext = items.size() > limit;
        if (hasNext) {
            items = items.subList(0, limit);
        }

        String nextCursor = hasNext ? encodeCursor(idExtractor.apply(items.getLast())) : null;

        return new CursorPagination<>(items, hasNext, nextCursor);
    }

    public static boolean isFirstCursor(Long cursorId) {
        return cursorId == null || cursorId == Long.MAX_VALUE;
    }
}
