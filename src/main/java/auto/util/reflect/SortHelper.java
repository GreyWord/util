package auto.util.reflect;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SortHelper {

    /**按第二个List的顺序，获取第一个List的对应字段并排序(如id)(注意id和实体必须唯一对应)
     * <br/>主要用于解决jpa FindByIDin入参和出参顺序不一致的问题*/
    public static <T,V> List<T> sort(List<T> list1,List<V> list2){
        return sort(list1,list2,"id");
    }
    public static <T,V> List<T> sort(List<T> list1,List<V> list2,String field){
        Map<Object, T> map = toMap(list1, field);
        List<T> list = new ArrayList<>();
        list2.forEach(key->list.add(map.get(key)));
        return list;
    }
    public static <T,V> List<T> sort(List<T> list1,List<V> list2,String field1,String field2){
        return sort(list1,Reflect_tools.getFieldValueList(list2, field2),field1);
    }

    /**按照指定字段值去重*/
    public static <T> List<T> distinct(List<T> list,String field){
        return new ArrayList<>(toMap(list,field).values());
    }
    /**list1的key1和list2的key2作为主键对应，把list1中field1的值改为list2中field2的值*/
    public static <T,V> void change(List<T> list1,List<V> list2,String key1,String key2,String field1,String field2){
        Map<Object, T> aMap = toMap(list1, key1);
        list2.forEach(obj->
                Reflect_tools.setFieldValue(
                        aMap.get(Reflect_tools.getFieldValue(obj,key2))
                        ,field1
                        ,Reflect_tools.getFieldValue(obj,field2)
                )
        );
    }

    /**根据指定field把list转化为映射关系，value值相同时只保留最后一个*/
    public static <T> Map<Object,T> toMap(List<T> list, String field){
        Map<Object,T> map = new LinkedHashMap<>();
        list.forEach(obj->{
            if(obj!=null)
                map.put(Reflect_tools.getFieldValue(obj,field),obj);
        });
        return map;
    }
}
