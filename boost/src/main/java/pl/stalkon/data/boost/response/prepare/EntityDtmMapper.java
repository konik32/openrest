package pl.stalkon.data.boost.response.prepare;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.util.StringUtils;

public class EntityDtmMapper {

	private Logger log = LoggerFactory.getLogger(EntityDtmMapper.class);

	public Map<String, Object> mapEntityToDtm(Object entity, Class<?> clazz,
			List<ViewPropertyPath> propertiesToMap) {
		Map<String, Object> dtm = new HashMap<String, Object>(
				propertiesToMap.size());
		for (ViewPropertyPath prop : propertiesToMap) {
			try {
				decideForNestedObject(prop, dtm, entity);
			} catch (SecurityException e) {
				log.error(e.getMessage(), e);
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				log.error(e.getMessage(), e);
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				log.error(e.getMessage(), e);
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				log.error(e.getMessage(), e);
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				log.error(e.getMessage(), e);
				e.printStackTrace();
			}

		}
		return dtm;
	}

	public List<Object> mapEntitiesToDtm(List<?> entities, Class<?> clazz,
			List<ViewPropertyPath> propertiesToMap) {
		List<Object> result = new ArrayList<Object>(entities.size());
		for (Object entity : entities) {
			result.add(mapEntityToDtm(entity, clazz, propertiesToMap));
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private void decideForNestedObject(ViewPropertyPath prop, Map<String, Object> dtm,
			Object entity) throws SecurityException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
			PropertyPath propertyPath = prop.getCurrentPropertyPath();
			Method fieldMethod = getGetterMethod(propertyPath.getSegment(), propertyPath.getOwningType().getType());
			Object fieldObject = entity == null? null: fieldMethod.invoke(entity);
			if(dtm.containsKey(propertyPath.getSegment())){
				if(propertyPath.hasNext()){
					decideForNestedObject(prop.next(),(Map<String, Object>) dtm.get(propertyPath.getSegment()), fieldObject);
				}
			}else{
				if(propertyPath.hasNext()){
					Map<String, Object> childDtm = new HashMap<String, Object>();
					dtm.put(propertyPath.getSegment(), childDtm);
					decideForNestedObject(prop.next(), childDtm, fieldObject);
				}else{
					dtm.put(propertyPath.getSegment(), fieldObject);
				}
			}
	}

	private Method getGetterMethod(String fieldName, Class<?> clazz)
			throws SecurityException, NoSuchMethodException {
		return clazz.getMethod("get" + StringUtils.capitalize(fieldName));
	}
}
