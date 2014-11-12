package openrest.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import openrest.httpquery.parser.FilterWrapper;
import openrest.httpquery.parser.Parsers;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.context.PersistentEntities;

public class StaticFilterFactory implements InitializingBean {

	@Autowired
	private PersistentEntities persistentEntities;

	private Map<Class<?>, FilterWrapper> staticFilters = new HashMap<Class<?>, FilterWrapper>();

	@Override
	public void afterPropertiesSet() throws Exception {
		registerStaticFilters();
	}

	public List<FilterWrapper> get(Class<?> type) {
		List<FilterWrapper> filterWrappers = new ArrayList<FilterWrapper>();
		for (Class<?> filteredClass : staticFilters.keySet()) {
			if (filteredClass.isAssignableFrom(type)) {
				filterWrappers.add(staticFilters.get(filteredClass));
			}
		}
		return filterWrappers;
	}

	private void registerStaticFilters() {
		for (PersistentEntity persistentEntity : persistentEntities) {
			StaticFilter staticFilter = (StaticFilter) persistentEntity.findAnnotation(StaticFilter.class);
			if (staticFilter != null) {
				staticFilters.put(persistentEntity.getType(), create(staticFilter));
			} else {
				StaticFilters sFilters = (StaticFilters) persistentEntity.findAnnotation(StaticFilters.class);
				if (sFilters != null)
					for (StaticFilter filter : sFilters.filters()) {
						staticFilters.put(persistentEntity.getType(), create(filter));
					}
			}
		}
	}

	private FilterWrapper create(StaticFilter staticFilter) {
		FilterWrapper filterWrapper = new FilterWrapper(Parsers.parseFilter(staticFilter.value()), staticFilter.name());
		return filterWrapper;
	}

	// private FilterWrapper addAlias(FilterWrapper filterWrapper, String
	// alias){
	// TempPart tempPart = addAliasRecursively(filterWrapper.getTempPart(),
	// alias);
	// return new FilterWrapper(tempPart, filterWrapper.getValues(),
	// filterWrapper.getName());
	// }
	//
	// private TempPart addAliasRecursively(TempPart tempPart, String alias){
	// if(tempPart.getType().equals(TempPart.Type.LEAF))
	// return new TempPart(tempPart.getFunctionName(), alias != null? alias +
	// "." + tempPart.getPropertyName(): tempPart.getPropertyName(),
	// tempPart.getParametersCount());
	// else{
	// TempPart newTempPart = new TempPart(tempPart.getType(),
	// tempPart.getParts().size());
	// for(TempPart tp: tempPart.getParts()){
	// newTempPart.addPart(addAliasRecursively(tp, alias));
	// }
	// return newTempPart;
	// }
	// }

}
