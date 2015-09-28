package pl.openrest.dto.dummy.model;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Getter
@Setter
public class Tag extends AbstractPersistable<Long> {

    /**
	 * 
	 */
    private static final long serialVersionUID = -1404885138473687588L;
    private String name;

}
