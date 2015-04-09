package orest.security.matcher;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.Path;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.data.rest.webmvc.BaseUri;

public class RequestMatcherFactory {

	@Autowired
	private BaseUri baseUri;
	@Autowired
	private ResourceMappings mappings;

	public OrestRequestMatcher getOne(Class<?> entityType, String projection) {
		Path path = mappings.getMappingFor(entityType).getPath();
		path = path.slash("((?!search$)[^\\/]*)");
		OrestRequestMatcher matcher = new OrestRequestMatcher(baseUri, Pattern.compile("^" + path.toString() + "$"),
				"GET");
		matcher.setProjection(projection);
		return matcher;
	}

	public OrestRequestMatcher getCollection(Class<?> entityType, String projection) {
		Path path = mappings.getMappingFor(entityType).getPath();
		OrestRequestMatcher matcher = new OrestRequestMatcher(baseUri, Pattern.compile("^" + path.toString() + "$"),
				"GET");
		matcher.setProjection(projection);
		return matcher;
	}

	public OrestRequestMatcher post(Class<?> entityType, String dto) {
		Path path = mappings.getMappingFor(entityType).getPath();
		OrestRequestMatcher matcher = new OrestRequestMatcher(baseUri, Pattern.compile("^" + path.toString() + "$"),
				"POST");
		matcher.setDto(dto);
		return matcher;
	}

	public OrestRequestMatcher patch(Class<?> entityType, String dto) {
		Path path = mappings.getMappingFor(entityType).getPath();
		path.slash("[^\\/]*");
		OrestRequestMatcher matcher = new OrestRequestMatcher(baseUri, Pattern.compile("^" + path.toString() + "$"),
				"PATCH");
		matcher.setDto(dto);
		return matcher;
	}

	public OrestRequestMatcher put(Class<?> entityType, String dto) {
		Path path = mappings.getMappingFor(entityType).getPath();
		path.slash("[^\\/]*");
		OrestRequestMatcher matcher = new OrestRequestMatcher(baseUri, Pattern.compile("^" + path.toString() + "$"),
				"PUT");
		matcher.setDto(dto);
		return matcher;
	}

	public OrestRequestMatcher post(Class<?> entityType) {
		return post(entityType, null);
	}

	public OrestRequestMatcher patch(Class<?> entityType) {
		return patch(entityType, null);
	}

	public OrestRequestMatcher put(Class<?> entityType) {
		return put(entityType, null);
	}

	public OrestRequestMatcher getOne(Class<?> entityType) {
		return getOne(entityType, null);
	}

	public OrestRequestMatcher getCollection(Class<?> entityType) {
		return getCollection(entityType, null);
	}
}
