package pl.stalkon.data.boost.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.cypher.internal.compiler.v2_1.ast.rewriters.addUniquenessPredicates;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.context.PersistentEntities;

import pl.stalkon.data.boost.httpquery.parser.FilterParser;
import pl.stalkon.data.boost.httpquery.parser.FilterWrapper;
import pl.stalkon.data.boost.httpquery.parser.TempPart;

public class StaticFilterFactory implements InitializingBean{

	@Autowired
	private PersistentEntities persistentEntities;
	
	private Map<Class<?>, FilterWrapper> staticFilters = new HashMap<Class<?>, FilterWrapper>();
	
	@Override
	public void afterPropertiesSet() throws Exception {
		registerStaticFilters();
	}
	
	public List<FilterWrapper> get(Class<?> type, String alias){
		List<FilterWrapper> filterWrappers = new ArrayList<FilterWrapper>();
		for(Class<?> filteredClass: staticFilters.keySet()){
			if(filteredClass.isAssignableFrom(type)){
				filterWrappers.add(addAlias(staticFilters.get(filteredClass), alias));
			}
		}
		return filterWrappers;
	}
	
	
	private void registerStaticFilters(){
		for(PersistentEntity persistentEntity: persistentEntities){
			StaticFilter staticFilter = (StaticFilter) persistentEntity.findAnnotation(StaticFilter.class);
			if(staticFilter != null){
				FilterWrapper filterWrapper = create(staticFilter);
				filterWrapper.setName(staticFilter.name());
				staticFilters.put(persistentEntity.getType(),filterWrapper);
			}
		}
	}
	
	private FilterWrapper create(StaticFilter staticFilter){
		FilterParser filterParser = new FilterParser(staticFilter.value());
		return filterParser.parse();
	}
	
	
	private FilterWrapper addAlias(FilterWrapper filterWrapper, String alias){
		TempPart tempPart = addAliasRecursively(filterWrapper.getTempPart(), alias);
		return new FilterWrapper(tempPart, filterWrapper.getValues(), filterWrapper.getName());
	}
	
	private TempPart addAliasRecursively(TempPart tempPart, String alias){
		if(tempPart.getType().equals(TempPart.Type.LEAF))
			return new TempPart(tempPart.getFunctionName(), alias != null? alias + "." + tempPart.getPropertyName(): tempPart.getPropertyName(), tempPart.getParametersCount());
		else{
			TempPart newTempPart = new TempPart(tempPart.getType(), tempPart.getParts().size());
			for(TempPart tp: tempPart.getParts()){
				newTempPart.addPart(addAliasRecursively(tp, alias));
			}
			return newTempPart;
		}
	}
	
	


}
