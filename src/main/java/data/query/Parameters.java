package data.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.ParameterOutOfBoundsException;

/**
 * Modification of {@link org.springframework.data.repository.query.Parameters}
 * Object is created from {@link List} of {@link Parameter}s and sort and pageable indexes
 * @author Szymon Konicki
 *
 */

public abstract class Parameters<S extends Parameters<S, T>, T extends Parameter>
		implements Iterable<T> {
	@SuppressWarnings("unchecked")
	public static final List<Class<?>> TYPES = Arrays.asList(Pageable.class,
			Sort.class);
	private final int pageableIndex;
	private final int sortIndex;
	private final List<T> parameters;

	public Parameters(List<T> parameters, int sortIndex, int pageableIndex) {
		this.parameters = parameters;
		this.sortIndex = sortIndex;
		this.pageableIndex = pageableIndex;
	}

	/**
	 * Creates a new {@link Parameters} instance with the given
	 * {@link Parameter}s put into new context.
	 * 
	 * @param originals
	 */
	protected Parameters(List<T> originals) {

		this.parameters = new ArrayList<T>();

		int pageableIndexTemp = -1;
		int sortIndexTemp = -1;

		for (int i = 0; i < originals.size(); i++) {

			T original = originals.get(i);
			this.parameters.add(original);

			pageableIndexTemp = original.isPageable() ? i : -1;
			sortIndexTemp = original.isSort() ? i : -1;
		}

		this.pageableIndex = pageableIndexTemp;
		this.sortIndex = sortIndexTemp;
	}

	/**
	 * Returns whether the {@link Pageable} exists.
	 * 
	 * @return
	 */
	public boolean hasPageableParameter() {
		return pageableIndex != -1;
	}

	/**
	 * Returns the index of the {@link Pageable} parameter if available or
	 * {@literal -1}
	 * 
	 * @return the pageableIndex
	 */
	public int getPageableIndex() {
		return pageableIndex;
	}

	/**
	 * Returns the index of the {@link Sort} parameter if available or
	 * {@literal -1}
	 * 
	 * @return the sortIndex
	 */
	public int getSortIndex() {
		return sortIndex;
	}

	/**
	 * Returns whether the {@link Sort} exists.
	 * 
	 * @return
	 */
	public boolean hasSortParameter() {
		return sortIndex != -1;
	}

	/**
	 * Returns whether we potentially find a {@link Sort} parameter in the
	 * parameters.
	 * 
	 * @return
	 */
	public boolean potentiallySortsDynamically() {
		return hasSortParameter() || hasPageableParameter();
	}

	/**
	 * Returns the parameter with the given index.
	 * 
	 * @param index
	 * @return
	 */
	public T getParameter(int index) {

		try {
			return parameters.get(index);
		} catch (IndexOutOfBoundsException e) {
			throw new ParameterOutOfBoundsException(
					"Invalid parameter index! You seem to have declare too little query method parameters!",
					e);
		}
	}

	/**
	 * Returns whether we have a parameter at the given position.
	 * 
	 * @param position
	 * @return
	 */
	public boolean hasParameterAt(int position) {

		try {
			return null != getParameter(position);
		} catch (ParameterOutOfBoundsException e) {
			return false;
		}
	}

	/**
	 * Returns whether parameters contains one of the special parameters (
	 * {@link Pageable}, {@link Sort}).
	 * 
	 * @return
	 */
	public boolean hasSpecialParameter() {
		return hasSortParameter() || hasPageableParameter();
	}

	/**
	 * Returns the number of parameters.
	 * 
	 * @return
	 */
	public int getNumberOfParameters() {
		return parameters.size();
	}

	/**
	 * Returns a {@link Parameters} instance with effectively all special
	 * parameters removed.
	 * 
	 * @return
	 * @see Parameter#TYPES
	 * @see Parameter#isSpecialParameter()
	 */
	public S getBindableParameters() {

		List<T> bindables = new ArrayList<T>();

		for (T candidate : this) {

			if (candidate.isBindable()) {
				bindables.add(candidate);
			}
		}

		return createFrom(bindables);
	}

	protected abstract S createFrom(List<T> parameters);

	/**
	 * Returns a bindable parameter with the given index. So for a method with a
	 * signature of {@code (Pageable pageable, String name)} a call to
	 * {@code #getBindableParameter(0)} will return the {@link String}
	 * parameter.
	 * 
	 * @param bindableIndex
	 * @return
	 */
	public T getBindableParameter(int bindableIndex) {
		return getBindableParameters().getParameter(bindableIndex);
	}

	/**
	 * Returns whether the given type is a bindable parameter.
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isBindable(Class<?> type) {
		return !TYPES.contains(type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<T> iterator() {
		return parameters.iterator();
	}
}
