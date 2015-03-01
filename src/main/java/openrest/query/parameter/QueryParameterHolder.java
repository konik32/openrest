package openrest.query.parameter;




import openrest.data.query.parser.OpenRestPartTree;
import data.query.JpaParameters;

public class QueryParameterHolder {
	
	private final OpenRestPartTree tree;
	private final JpaParameters jpaParameters;
	private final Object[] values;

	public QueryParameterHolder(OpenRestPartTree tree, Object values[], JpaParameters jpaParameters) {
		this.values = values;
		this.tree = tree;
		this.jpaParameters = jpaParameters;
	}

	public OpenRestPartTree getPartTree() {
		return tree;
	}

	public Object[] getValues() {
		return values;
	}

	public JpaParameters getJpaParameters() {
		return jpaParameters;
	}

}
