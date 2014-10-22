package pl.stalkon.data.boost.httpquery.parser;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mapping.PropertyPath;

public class ViewsParser {

	private final String views;
	private final Class<?> domainClass;
	private List<PropertyPath> viewsPropertyPaths;
	public ViewsParser(String views, Class<?> domainClass) {
		super();
		this.views = views;
		this.domainClass = domainClass;
	}
	
	public List<PropertyPath> parse() {
		if (views == null)
			return null;
		String parts[] = views.split(",");
		List<PropertyPath> viewsPropertyPaths = new ArrayList<PropertyPath>(
				parts.length);
		for (String part : parts) {
			part = part.trim();
			viewsPropertyPaths.add(PropertyPath.from(part, domainClass));
		}
		return viewsPropertyPaths;
	}


}
