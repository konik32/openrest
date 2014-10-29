package data.query;

import java.util.Date;

import javax.persistence.TemporalType;

import org.springframework.data.jpa.repository.query.JpaParameters;

/**
 * 
 * Modification of {@link JpaParameters.JpaParameter} to implement
 * {@link Parameter}
 * 
 * @author Szymon Konicki
 */
public class JpaParameter extends Parameter {

	private TemporalType temporalType;

	public JpaParameter(Class<?> type, String name, int index, TemporalType temporalType) {
		super(type, name, index);
		this.temporalType = temporalType;
	}

	public JpaParameter(Class<?> type, int index, TemporalType temporalType) {
		this(type, "p" + index, index, temporalType);
	}

	public JpaParameter(Class<?> type, int index) {
		this(type, "p" + index, index, null);
	}

	public JpaParameter(Class<?> type, String name, int index) {
		this(type, name, index, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.repository.query.Parameter#isBindable()
	 */
	@Override
	public boolean isBindable() {
		return super.isBindable() || isTemporalParameter();
	}

	/**
	 * @return {@literal true} if this parameter is of type {@link Date} and has
	 *         an {@link TemporalType}.
	 */
	public boolean isTemporalParameter() {
		return isDateParameter() && hasTemporalType();
	}

	/**
	 * @return the {@link TemporalType}.
	 */
	public TemporalType getTemporalType() {
		return this.temporalType;
	}

	private boolean hasTemporalType() {
		return temporalType != null;
	}

	private boolean isDateParameter() {
		return getType().equals(Date.class);
	}
}
