package data.query;

import java.util.Date;
import java.util.Iterator;

import javax.persistence.Parameter;
import javax.persistence.Query;

import org.springframework.data.repository.query.Parameters;
import org.springframework.util.Assert;
import data.jpa.query.ParameterMetadataProvider.ParameterMetadata;

/**
 * Modification of
 * {@link org.springframework.data.jpa.repository.query.CriteriaQueryParameterBinder}
 * to extend {@link ParameterBinder}
 * 
 * @author Szymon Konicki
 *
 */
public class CriteriaQueryParameterBinder extends ParameterBinder {
	private final Iterator<ParameterMetadata<?>> expressions;

	/**
	 * Creates a new {@link CriteriaQueryParameterBinder} for the given
	 * {@link Parameters}, values and some
	 * {@link javax.persistence.criteria.ParameterExpression}.
	 * 
	 * @param parameters
	 * @param values
	 * @param expressions
	 */
	public CriteriaQueryParameterBinder(JpaParameters parameters, Object[] values, Iterable<ParameterMetadata<?>> expressions) {
		super(parameters, values);
		Assert.notNull(expressions);
		this.expressions = expressions.iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.jpa.repository.query.ParameterBinder#bind(javax
	 * .persistence.Query,
	 * org.springframework.data.jpa.repository.query.JpaParameters.JpaParameter,
	 * java.lang.Object, int)
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void bind(Query query, JpaParameter parameter, Object value, int position) {

		ParameterMetadata<Object> metadata = (ParameterMetadata<Object>) expressions.next();

		if (metadata.isIsNullParameter()) {
			return;
		}

		if (parameter.isTemporalParameter()) {
			query.setParameter((Parameter<Date>) (Object) metadata.getExpression(), (Date) metadata.prepare(value), parameter.getTemporalType());
		} else {
			query.setParameter(metadata.getExpression(), metadata.prepare(value));
		}
	}
}
