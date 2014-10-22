package pl.stalkon.data.query;

import static java.lang.String.format;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;

public class Parameter {

	@SuppressWarnings("unchecked") public static final List<Class<?>> TYPES = Arrays.asList(Pageable.class, Sort.class);

	private static final String NAMED_PARAMETER_TEMPLATE = ":%s";
	private static final String POSITION_PARAMETER_TEMPLATE = "?%s";
	private final Class<?> type;
	private final String name;
	private final int index;
	
	
	public Parameter(Class<?> type, String name, int index) {
		Assert.notNull(type);
		Assert.state(index >= 0);
		this.type = type;
		this.name = name;
		this.index = index;
	}
	
	
	public Parameter(Class<?> type, int index) {
		this(type, null, index);
	}

	/**
	 * Returns whether the {@link Parameter} is the first one.
	 * 
	 * @return
	 */
	public boolean isFirst() {
		return getIndex() == 0;
	}

	/**
	 * Returns whether the parameter is a special parameter.
	 * 
	 * @return
	 * @see #TYPES
	 */
	public boolean isSpecialParameter() {
		return TYPES.contains(type);
	}

	/**
	 * Returns whether the {@link Parameter} is to be bound to a query.
	 * 
	 * @return
	 */
	public boolean isBindable() {
		return !isSpecialParameter();
	}

	/**
	 * Returns the placeholder to be used for the parameter. Can either be a named one or positional.
	 * 
	 * @return
	 */
	public String getPlaceholder() {

		if (isNamedParameter()) {
			return format(NAMED_PARAMETER_TEMPLATE, getName());
		} else {
			return format(POSITION_PARAMETER_TEMPLATE, getIndex());
		}
	}

	/**
	 * Returns the position index the parameter is bound to in the context of its surrounding {@link Parameters}.
	 * 
	 * @return
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Returns whether the parameter is annotated with {@link Param}.
	 * 
	 * @return
	 */
	public boolean isNamedParameter() {
		return !isSpecialParameter() && getName() != null;
	}

	/**
	 * Returns the name of the parameter
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the type of the {@link Parameter}.
	 * 
	 * @return the type
	 */
	public Class<?> getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return format("%s:%s", isNamedParameter() ? getName() : "#" + getIndex(), getType().getName());
	}

	/**
	 * Returns whether the {@link Parameter} is a {@link Pageable} parameter.
	 * 
	 * @return
	 */
	boolean isPageable() {
		return Pageable.class.isAssignableFrom(getType());
	}

	/**
	 * Returns whether the {@link Parameter} is a {@link Sort} parameter.
	 * 
	 * @return
	 */
	boolean isSort() {
		return Sort.class.isAssignableFrom(getType());
	}
}