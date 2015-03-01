package orest.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Getter
@Setter
@Entity
public class Tag extends AbstractPersistable<Long>{

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1404885138473687588L;
	@Column(length=64, nullable=false)
	private String name;
	
	
}
