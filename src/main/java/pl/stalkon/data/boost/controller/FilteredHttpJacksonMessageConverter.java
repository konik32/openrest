package pl.stalkon.data.boost.controller;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ResponseBody;

import pl.stalkon.data.boost.response.Response;
import pl.stalkon.data.boost.response.filter.FilteringObjectMapper;
import pl.stalkon.data.boost.response.filter.RequestBasedFilter;
import pl.stalkon.data.boost.response.filter.RequestBasedFilterIntrospector;
import pl.stalkon.data.boost.response.filter.ResponseFilterInvoker;
import pl.stalkon.data.boost.response.filter.WrappedResponse;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

public class FilteredHttpJacksonMessageConverter extends
		AbstractHttpMessageConverter<Object> {

	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	private ObjectMapper objectMapper = new ObjectMapper();

	private String jsonPrefix;

	private Boolean prettyPrint;
	
	@Autowired
	private ResponseFilterInvoker responseFilterInvoker;
	
	@Autowired
	private HttpServletRequest request;
	
	public FilteredHttpJacksonMessageConverter(ObjectMapper objectMapper) {
		this();
		this.objectMapper = new FilteringObjectMapper(objectMapper);
	}

	public FilteredHttpJacksonMessageConverter() {
		super(new MediaType("application", "json", DEFAULT_CHARSET),
				new MediaType("application", "*+json", DEFAULT_CHARSET));
	}

	@Override
	protected void writeInternal(Object response,
			HttpOutputMessage outputMessage) throws IOException,
			HttpMessageNotWritableException {
		JsonEncoding encoding = getJsonEncoding(outputMessage.getHeaders()
				.getContentType());
		JsonGenerator jsonGenerator = this.objectMapper.getFactory()
				.createGenerator(outputMessage.getBody(), encoding);
		// A workaround for JsonGenerators not applying serialization features
		// https://github.com/FasterXML/jackson-databind/issues/12
		if (this.objectMapper.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
			jsonGenerator.useDefaultPrettyPrinter();
		}
		try {
			if (this.jsonPrefix != null) {
				jsonGenerator.writeRaw(this.jsonPrefix);
			}
			this.objectMapper.writer(getFilterProvider()).writeValue(
					jsonGenerator, response);
		} catch (JsonProcessingException ex) {
			throw new HttpMessageNotWritableException("Could not write JSON: "
					+ ex.getMessage(), ex);
		}

	}

	private SimpleFilterProvider getFilterProvider() {
		SimpleFilterProvider filterProvider = new SimpleFilterProvider();
		filterProvider.addFilter("requestBasedFilter", new RequestBasedFilter(responseFilterInvoker, request));
		return filterProvider;
	}

	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return false;
	}

	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return Response.class.isAssignableFrom(clazz);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Object readInternal(Class<? extends Object> clazz,
			HttpInputMessage inputMessage) throws IOException,
			HttpMessageNotReadableException {
		throw new UnsupportedOperationException();
	}

	protected JavaType getJavaType(Type type, Class<?> contextClass) {
		return (contextClass != null) ? this.objectMapper.getTypeFactory()
				.constructType(type, contextClass) : this.objectMapper
				.constructType(type);
	}

	protected JsonEncoding getJsonEncoding(MediaType contentType) {
		if (contentType != null && contentType.getCharSet() != null) {
			Charset charset = contentType.getCharSet();
			for (JsonEncoding encoding : JsonEncoding.values()) {
				if (charset.name().equals(encoding.getJavaName())) {
					return encoding;
				}
			}
		}
		return JsonEncoding.UTF8;
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = new FilteringObjectMapper(objectMapper);
	}

	private void configurePrettyPrint() {
		if (this.prettyPrint != null) {
			this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT,
					this.prettyPrint);
		}
	}

	/**
	 * Specify a custom prefix to use for this view's JSON output. Default is
	 * none.
	 * 
	 * @see #setPrefixJson
	 */
	public void setJsonPrefix(String jsonPrefix) {
		this.jsonPrefix = jsonPrefix;
	}

	/**
	 * Indicate whether the JSON output by this view should be prefixed with
	 * "{} &&". Default is false.
	 * <p>
	 * Prefixing the JSON string in this manner is used to help prevent JSON
	 * Hijacking. The prefix renders the string syntactically invalid as a
	 * script so that it cannot be hijacked. This prefix does not affect the
	 * evaluation of JSON, but if JSON validation is performed on the string,
	 * the prefix would need to be ignored.
	 * 
	 * @see #setJsonPrefix
	 */
	public void setPrefixJson(boolean prefixJson) {
		this.jsonPrefix = (prefixJson ? "{} && " : null);
	}

	/**
	 * Whether to use the {@link DefaultPrettyPrinter} when writing JSON. This
	 * is a shortcut for setting up an {@code ObjectMapper} as follows:
	 * 
	 * <pre>
	 * ObjectMapper mapper = new ObjectMapper();
	 * mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
	 * converter.setObjectMapper(mapper);
	 * </pre>
	 */
	public void setPrettyPrint(boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
		configurePrettyPrint();
	}
}
