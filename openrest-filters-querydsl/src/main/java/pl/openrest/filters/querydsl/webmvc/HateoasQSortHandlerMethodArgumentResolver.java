package pl.openrest.filters.querydsl.webmvc;

import org.springframework.core.MethodParameter;
import org.springframework.data.querydsl.QSort;
import org.springframework.data.web.HateoasSortHandlerMethodArgumentResolver;
import org.springframework.web.util.UriComponentsBuilder;

public class HateoasQSortHandlerMethodArgumentResolver extends HateoasSortHandlerMethodArgumentResolver {

	@Override
	public void enhance(UriComponentsBuilder builder, MethodParameter parameter, Object value) {
		if (value instanceof QSort) {
			return;
		}
		super.enhance(builder, parameter, value);
	}
}
