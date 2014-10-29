package data.query.parser;

import org.springframework.data.domain.Sort;
/**
 * Modification of {@link org.springframework.data.repository.query.parser.PartTree} to create it from any source.
 * @author Szymon Konicki
 *
 */
public class PartTree {

	private Sort sort;
	private Boolean distinct;
	private Boolean countProjection;
	private TreeBranch partTreeRoot;

	public PartTree(TreeBranch partTreeRoot) {
		this(partTreeRoot, null, null, null);
	}

	public PartTree(TreeBranch partTreeRoot, Sort sort) {
		this(partTreeRoot, sort, null, null);
	}

	public PartTree(TreeBranch partTreeRoot, Sort sort, Boolean distinct,
			Boolean countProjection) {
		this.partTreeRoot = partTreeRoot;
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
		return distinct == null ? false : distinct;
	}

	/**
	 * Returns whether a count projection shall be applied.
	 * 
	 * @return
	 */
	public Boolean isCountProjection() {
		return countProjection == null ? false : countProjection;
	}

	public TreeBranch getPartTreeRoot() {
		return partTreeRoot;
	}
	
	@Override
	public String toString() {
		return String.format("Sort: %s distinct: %s countProjection: %s  %s", sort, distinct, countProjection, partTreeRoot);
	}

}
