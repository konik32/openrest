package openrest.query;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import openrest.httpquery.parser.Parsers;
import openrest.httpquery.parser.TempPart;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.context.PersistentEntities;

public class StaticFilterFactory implements InitializingBean {

	private PersistentEntities persistentEntities;

	private StaticFilterInclusionManager inclusionManager;

	protected Map<Class<?>, StaticFilterWrapper> staticFilters = new HashMap<Class<?>, StaticFilterWrapper>();

	@Override
	public void afterPropertiesSet() throws Exception {
		registerStaticFilters();
	}

	public List<StaticFilterWrapper> get(Class<?> type, String alias) {
		List<StaticFilterWrapper> filterWrappers = new ArrayList<StaticFilterWrapper>();
		for (Class<?> filteredClass : staticFilters.keySet()) {
			if (filteredClass.isAssignableFrom(type)) {
				StaticFilterWrapper filterWrapper = staticFilters.get(filteredClass);
				if (alias != null)
					filterWrapper = addAlias(filterWrapper, alias);
				if (includeFilter(filterWrapper))
					filterWrappers.add(filterWrapper);
			}
		}
		return filterWrappers;
	}

	protected void registerStaticFilters() {
		for (PersistentEntity persistentEntity : persistentEntities) {
			StaticFilter staticFilter = (StaticFilter) persistentEntity.findAnnotation(StaticFilter.class);
			if (staticFilter != null) {
				staticFilters.put(persistentEntity.getType(), create(staticFilter));
			} else {
				StaticFilters sFilters = (StaticFilters) persistentEntity.findAnnotation(StaticFilters.class);
				if (sFilters != null)
					addFilterCollection(sFilters, persistentEntity.getType());
			}
		}
	}

	protected StaticFilterWrapper create(StaticFilter staticFilter) {
		return new StaticFilterWrapper(Parsers.parseStaticFilter(staticFilter.value()), staticFilter.name(), staticFilter.condition());
	}

	protected boolean includeFilter(StaticFilterWrapper filterWrapper) {
		if (inclusionManager != null)
			return inclusionManager.includeFilter(filterWrapper);
		return true;
	}

	protected void addFilterCollection(StaticFilters ann, Class<?> type) {
		for (StaticFilter filter : ann.filters()) {
			staticFilters.put(type, create(filter));
		}

	}

	private StaticFilterWrapper addAlias(StaticFilterWrapper filterWrapper, String alias) {
		TempPart tempPart = addAliasRecursively(filterWrapper.getTempPart(), alias);
		return new StaticFilterWrapper(tempPart, filterWrapper.getName(), filterWrapper.getCondition());
	}

	private TempPart addAliasRecursively(TempPart tempPart, String alias) {
		if (tempPart.getType().equals(TempPart.Type.LEAF))
			return new TempPart(tempPart.getFunctionName(), alias != null ? alias + "." + tempPart.getPropertyName() : tempPart.getPropertyName(),
					tempPart.getParameters());
		else {
			TempPart newTempPart = new TempPart(tempPart.getType(), tempPart.getParts().size());
			for (TempPart tp : tempPart.getParts()) {
				newTempPart.addPart(addAliasRecursively(tp, alias));
			}
			return newTempPart;
		}
	}

	public void setPersistentEntities(PersistentEntities persistentEntities) {
		this.persistentEntities = persistentEntities;
	}

	public void setInclusionManager(StaticFilterInclusionManager inclusionManager) {
		this.inclusionManager = inclusionManager;
	}

}
