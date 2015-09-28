package pl.openrest.dto.dummy.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Getter
@Setter
public class Product extends AbstractPersistable<Long> {

    /**
	 * 
	 */
    private static final long serialVersionUID = -1248955369326613762L;

    private String name;

    private String description;

    private Integer productionYear;

    private User user;

    private List<Tag> tags;
}
