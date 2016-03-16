package pl.openrest.filters.querydsl.webmvc;

import org.springframework.data.querydsl.QPageRequest;
import org.springframework.data.querydsl.QSort;

import pl.openrest.filters.webmvc.DefaultedPageRequest;

import com.mysema.query.types.OrderSpecifier;

public class QDefaultedPageRequest extends QPageRequest implements DefaultedPageRequest {

    private static final long serialVersionUID = 1L;
    private boolean isDefault;

    public QDefaultedPageRequest(int page, int size) {
        super(page, size);
    }

    public QDefaultedPageRequest(int page, int size, OrderSpecifier<?>... orderSpecifiers) {
        super(page, size, orderSpecifiers);
    }

    public QDefaultedPageRequest(int page, int size, QSort sort) {
        super(page, size, sort);

    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}
