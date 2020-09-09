package auto.util.reflect;

import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;


/**
 * 属性相关工具
 * @author 张旭/GreyWord
 * @since 1.6 2016/12/19
 * @version 0.0.5
 * 
 */
public class Reflect_tools {
	
	/**
	 * 利用反射获取一个对象相应的属性值
	 */
	public static Object getFieldValue(Object obj, String fieldName){
		if(Empty_tools.hasNULL(obj,fieldName))	return null;
		return getFieldValue(obj, getFieldByName(obj.getClass(),fieldName));
	}
	public static Object getFieldValue(Object obj, Field field){
		if(Empty_tools.hasNULL(obj,field))	return null;
		
		Object value = null;
		try {//获取值
			field.setAccessible(true);
			value = field.get(obj);
		} catch (IllegalAccessException   e) {
			e.printStackTrace();
		}
		return value;
	}
	
	
	/**
	 * 利用反射修改一个对象相应的属性值
	 */
	public static void setFieldValue(Object obj, String fieldName, Object value){
		setFieldValue(obj, getFieldByName(obj.getClass(),fieldName), value);
	}
	public static void setFieldValue(Object obj, Field field, Object value){
		if(Empty_tools.hasNULL(value,obj,field)) return;
		try {
			//修改值
			field.setAccessible(true);
			field.set(obj,value);
		}catch (IllegalAccessException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 修改 obj 的属性值<br>
	 * 根据 hm 的内容配置<br>
	 */
	public static <T> T changeObjectByMap(T obj,Map<String, Object> hm){
		if(hm!=null && hm.size()>0){
			Map<String, Field> fm = getfieldNameMap(obj.getClass());
			for( Entry<String, Field> field:fm.entrySet()){
				setFieldValue(obj, field.getValue(), hm.get(field.getKey()));
			}
		}
		return obj; 
	}
	/**
	 * 根据 objclass 创建对象<br>
	 * 根据 hashmap 的内容配置对象的属性值<br>
	 */
	public static <T> T createObjectByMap(Class<T> objclass,Map<String, Object> hashmap){
		T obj = null;
		try {
			obj = objclass.newInstance();
			changeObjectByMap(obj,hashmap);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return obj; 
	}


	/**获取Field对象
	 * 存在继承关系时会递归调用
	 */
	public static Field getFieldByName(Class<?> objclass, String fieldName){
		Field field = null;
		try {
			field = objclass.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			if(objclass.getSuperclass()!=Object.class){
				field = getFieldByName(objclass.getSuperclass(),fieldName);
			}else {
				e.printStackTrace();
			}
		}
		return field;
	}

	/**
	 */
	public static Class<?> getFieldTypeByName(Class<?> objclass, String fieldName){
		Field field = getFieldByName(objclass, fieldName);
		if(field==null)
			return null;
		return field.getType();
	}
	/**
	 * 不显示没有值的属性（null||""）<br>
	 * 其他属性会按次序排列显示，开头是简单类名<br>
	 * 例如 Staff_info[ staff_id = 1, role_id = 2, staff_name = 小明]<br>
	 * 在staff_id为空时，将显示Staff_info[ role_id = 2, staff_name = 小明]<br>
	 */
	public static String getInfo(Object obj){
		StringBuilder info = new StringBuilder();
		
		Class<?> sc = obj.getClass();
		info.append(sc.getSimpleName());info.append("[");
		
		Map<String, Object> hm = getValueMap(obj);
		for(Entry<String, Object> field:hm.entrySet()){
			info.append(' ');
			info.append(field.getKey());
			info.append(" = ");
			info.append(field.getValue());
			info.append(',');
		}
		
		info.setCharAt(info.length()-1, ']');
		return info.toString();
	}
	
	/**
	 * 遍历obj内的属性及值，包括私有属性<br>
	 * 即使属性值为空也返回<br>
	 * @param obj
	 * @return 属性名和属性值的键值对
	 */
	public static Map<String, Object> getValueTotalMap(Object obj){
		HashMap<String, Object> hm = new HashMap<String, Object>();
		if(obj!=null){
			Class<?> sc = obj.getClass();
			Field[] sf = sc.getDeclaredFields();
			for(Field field:sf){
				Object value = getFieldValue(obj,field);
				hm.put(field.getName(), value);
			}
		}
		return hm;
	}
	
	/**
	 * 遍历obj内的属性及值，包括私有属性<br>
	 * 属性值为空（null或""）时不返回<br>
	 * 内部是HashMap<br>
	 * @param obj
	 * @return 属性名和属性值的键值对
	 */
	public static Map<String, String> getValueStringMap(Object obj){
		Map<String, String> hm = new HashMap<String, String>();
		if(obj!=null){
			Class<?> sc = obj.getClass();
			Field[] sf = sc.getDeclaredFields();
			for(Field field:sf){
				field.setAccessible(true);
				Object value = getFieldValue(obj,field);
				if(value!=null)
					hm.put(field.getName(), value.toString());
			}
		}
		return hm;
	}
	
	/**
	 * 遍历obj内的属性及值，包括私有属性<br>
	 * 属性值为空（null或""）时不返回<br>
	 * 内部是HashMap<br>
	 * @param obj
	 * @return 属性名和属性值的键值对
	 */
	public static Map<String, Object> getValueMap(Object obj){
		Map<String, Object> hm = new HashMap<String, Object>();
		if(obj!=null){
			Class<?> sc = obj.getClass();
			Field[] sf = sc.getDeclaredFields();
			
			for(Field field:sf){
				field.setAccessible(true);
				Object value = getFieldValue(obj,field);
				if(Empty_tools.notNULLString(value))
					hm.put(field.getName(), value);
			}
		}
		return hm;
	}
	
	/**
	 * 遍历objclass内的属性，包括私有属性<br>
	 * 内部是HashMap<br>
	 * @param objclass
	 * @return 属性名和Field的键值对
	 */
	public static Map<String, Field> getfieldNameMap(Class<?> objclass){
		Map<String, Field> hm = new HashMap<String, Field>();
		Field[] sf = objclass.getDeclaredFields();
		
		for(Field field:sf)
			hm.put(field.getName(), field);
		
		return hm;
	}
	
	/**
	 * 遍历objclass内的属性，包括私有属性<br>
	 * 内部是ArrayList<br>
	 * @param objclass
	 * @return 属性名的List表
	 */
	public static List<String> getfieldNameList(Class<?> objclass){
		List<String> hs = new ArrayList<String>();
		Field[] sf = objclass.getDeclaredFields();
		for(Field field:sf)
			hs.add(field.getName());
		return hs;
	}
	public static <T>  List<Object> getFieldValueList(List<T> list,String fieldName){
		if(list==null || fieldName==null)return Collections.emptyList();
		return list.stream().map(obj->getFieldValue(obj,fieldName)).collect(Collectors.toList());
	}
	
	
	/**
	 * 内部是ArrayList的toArray<br>
	 * @param objclass 信息存储类
	 * @param type 查询数据类型
	 * @return objclass类中是type子类的属性列表
	 */
	public static Set<String> getTypeSet(Class<?> objclass,Class<?> type){
		
		Set<String> types = new HashSet<String>();
		Field[] sf = objclass.getDeclaredFields();
		for(Field field:sf){
			if(type.isAssignableFrom(field.getType()))
				types.add(field.getName());
		}
		return types;
	}
}
