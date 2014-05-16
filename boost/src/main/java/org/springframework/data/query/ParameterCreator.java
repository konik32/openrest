package org.springframework.data.query;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface ParameterCreator {
	<T> List<T> createParameters();
	
	/**
	 * Returns the index of the {@link Pageable} {@link Method} parameter if available or {@literal -1} 
	 * 
	 * @return the pageableIndex
	 */	
	int getPageableIndex();
	
	/**
	 * Returns the index of the {@link Sort} parameter if available or {@literal -1} 
	 * 
	 * @return the sortIndex
	 */
	int getSortIndex();
}
