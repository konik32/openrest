package pl.stalkon.data.boost.parser;

import java.util.List;

import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.query.JpaParameters;
import org.springframework.data.query.parser.PartTree;

public interface PartTreeSpecificationParameterExtractor {
	
	PartTree getPartTree();
	JpaParameters getJpaParamters();
	Object[] getParamtersValues();
	List<PropertyPath> viewPropertyPaths();

}
