package org.springframework.data.query.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.query.parser.Part.Type;

public class PartTree implements Iterable<OrPart> {

	private Sort sort;
	private Boolean distinct;
	private Boolean countProjection;
	private List<OrPart> orParts = new ArrayList<OrPart>();

	public PartTree(List<OrPart> orParts) {
		this(orParts, null,  null, null);
	}
	
	public PartTree(List<OrPart> orParts, Sort sort){
		this(orParts, sort, null, null);
	}
	
	
	public PartTree(List<OrPart> orParts, Sort sort, Boolean distinct, Boolean countProjection){
		this.orParts = orParts;
		this.sort = sort;
		this.distinct = distinct;
		this.countProjection = countProjection;
	}

	/**
	 * Returns the {@link Sort} or <tt>null</tt>.
	 * 
	 * @return the sort
	 */
	public Sort getSort() {
		return sort;
	}


	/**
	 * Returns whether we indicate distinct lookup of entities.
	 * 
	 * @return {@literal true} if distinct
	 */
	public boolean isDistinct() {
		return distinct.equals(null) ? false : distinct;
	}

	/**
	 * Returns whether a count projection shall be applied.
	 * 
	 * @return
	 */
	public Boolean isCountProjection() {
		return countProjection.equals(null) ? false : countProjection;
	}

	/**
	 * Returns an {@link Iterable} of all parts contained in the
	 * {@link PartTree}.
	 * 
	 * @return the iterable {@link Part}s
	 */
	public Iterable<Part> getParts() {
		List<Part> result = new ArrayList<Part>();
		for (OrPart orPart : this) {
			for (Part part : orPart) {
				result.add(part);
			}
		}
		return result;
	}

	/**
	 * Returns all {@link Part}s of the {@link PartTree} of the given
	 * {@link Type}.
	 * 
	 * @param type
	 * @return
	 */
	public Iterable<Part> getParts(Type type) {

		List<Part> result = new ArrayList<Part>();

		for (Part part : getParts()) {
			if (part.getType().equals(type)) {
				result.add(part);
			}
		}

		return result;
	}

	public Iterator<OrPart> iterator() {
		return orParts.iterator();
	}
}
