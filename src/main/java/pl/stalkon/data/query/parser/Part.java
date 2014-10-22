package pl.stalkon.data.query.parser;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.mapping.PropertyPath;
import org.springframework.util.Assert;

public class Part implements TreePart {

	protected final PropertyPath propertyPath;
	protected final Part.Type type;

	private IgnoreCaseType ignoreCase = IgnoreCaseType.NEVER;

	public Part(String path, Type type, Class<?> domainClass) {
		this(path, type, domainClass, false);
	}

	public Part(String path, Type type, Class<?> domainClass,
			boolean alwaysIgnoreCase) {
		Assert.notNull(domainClass, "Type must not be null!");

		if (alwaysIgnoreCase && ignoreCase != IgnoreCaseType.ALWAYS) {
			this.ignoreCase = IgnoreCaseType.WHEN_POSSIBLE;
		}
		this.type = type;
		this.propertyPath = PropertyPath.from(path, domainClass);
	}

	/**
	 * Returns how many method parameters are bound by this part.
	 * 
	 * @return
	 */
	public int getNumberOfArguments() {
		return type.getNumberOfArguments();
	}

	/**
	 * @return the propertyPath
	 */
	public PropertyPath getProperty() {
		return propertyPath;
	}

	/**
	 * @return the type
	 */
	public Part.Type getType() {
		return type;
	}

	/**
	 * Returns whether the {@link PropertyPath} referenced should be matched
	 * ignoring case.
	 * 
	 * @return
	 */
	public IgnoreCaseType shouldIgnoreCase() {
		return ignoreCase;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj == this) {
			return true;
		}

		if (obj == null || !getClass().equals(obj.getClass())) {
			return false;
		}

		Part that = (Part) obj;
		return this.propertyPath.equals(that.propertyPath)
				&& this.type.equals(that.type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		int result = 37;
		result += 17 * propertyPath.hashCode();
		result += 17 * type.hashCode();
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return String.format("%s(%s)", type , propertyPath.getSegment());
	}

	/**
	 * The type of a method name part. Used to create query parts in various
	 * ways.
	 * 
	 * @author Oliver Gierke
	 */
	public static enum Type {
		BETWEEN(2), IS_NOT_NULL(0), IS_NULL(0), LESS_THAN, LESS_THAN_EQUAL, GREATER_THAN, GREATER_THAN_EQUAL, BEFORE, AFTER, NOT_LIKE, LIKE, STARTING_WITH, ENDING_WITH, CONTAINING, NOT_IN, IN, NEAR, WITHIN, REGEX, EXISTS(
				0), TRUE(0), FALSE(0), NEGATING_SIMPLE_PROPERTY, SIMPLE_PROPERTY;

		private final int numberOfArguments;

		/**
		 * Creates a new {@link Type} using the given keyword, number of
		 * arguments to be bound and operator. Keyword and operator can be
		 * {@literal null}.
		 * 
		 * @param numberOfArguments
		 * @param keywords
		 */
		private Type(int numberOfArguments) {

			this.numberOfArguments = numberOfArguments;
		}

		private Type() {

			this(1);
		}

		/**
		 * Returns the number of arguments the propertyPath binds. By default
		 * this exactly one argument.
		 * 
		 * @return
		 */
		public int getNumberOfArguments() {

			return numberOfArguments;
		}

	}

	/**
	 * The various types of ignore case that are supported.
	 * 
	 * @author Phillip Webb
	 */

	public enum IgnoreCaseType {

		/**
		 * Should not ignore the sentence case.
		 */
		NEVER,

		/**
		 * Should ignore the sentence case, throwing an exception if this is not
		 * possible.
		 */
		ALWAYS,

		/**
		 * Should ignore the sentence case when possible to do so, silently
		 * ignoring the option when not possible.
		 */
		WHEN_POSSIBLE
	}

	@Override
	public List<Part> getParts() {
		return Arrays.asList(this);
	}

	@Override
	public List<Part> getParts(Type type) {
		if (type.equals(this.type))
			return Arrays.asList(this);
		return null;
	}

}
