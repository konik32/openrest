package orest.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Getter
@Setter
@Entity
@Table(name = "product")
public class Product extends AbstractPersistable<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1248955369326613762L;

	private String name;

	private String description;

	@Column(name="production_year")
	private Integer productionYear;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name = "product_tag_maps", joinColumns = { @JoinColumn(name = "product_id", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "tag_id", referencedColumnName = "id") }, uniqueConstraints = { @UniqueConstraint(name = "product_tag_uq", columnNames = {
			"product_id", "tag_id" }) })
	private List<Tag> tags;
}
