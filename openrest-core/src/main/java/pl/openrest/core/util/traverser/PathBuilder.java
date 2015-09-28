package pl.openrest.core.util.traverser;

import org.springframework.util.StringUtils;

public final class PathBuilder {

	private static final String SEPARATOR = ".";
	private static final String COLLECTION_FORMAT = "%s[%d]";
	private static final String MAP_FORMAT = "%s(%s)";

	public static String appendTo(String current, int index) {
		if (!StringUtils.hasText(current))
			throw new IllegalArgumentException("Current path connot be null or empty");
		return String.format(COLLECTION_FORMAT, current, index);
	}

	public static String appendToMap(String current, String key) {
		if (!StringUtils.hasText(current) || !StringUtils.hasText(key))
			throw new IllegalArgumentException("Current path and key connot be null or empty");
		return String.format(MAP_FORMAT, current, key);
	}

	public static String appendTo(String current, String fieldName) {
		return !StringUtils.hasText(current) ? fieldName : current + SEPARATOR + fieldName;
	}
}
