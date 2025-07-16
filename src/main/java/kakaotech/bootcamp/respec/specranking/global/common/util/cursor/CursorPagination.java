package kakaotech.bootcamp.respec.specranking.global.common.util.cursor;

import java.util.List;

public record CursorPagination<T>(List<T> items, boolean hasNext, String nextCursor) {
}
