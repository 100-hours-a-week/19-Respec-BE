package kakaotech.bootcamp.respec.specranking.domain.comment.repository;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class CustomPageImpl<T> extends PageImpl<T> {

    private final Long customTotalElements;

    public CustomPageImpl(List<T> content, Pageable pageable, Long totalForPagination, Long customTotalElements) {
        super(content, pageable, totalForPagination);
        this.customTotalElements = customTotalElements;
    }

    @Override
    public long getTotalElements() { return customTotalElements; }
}
