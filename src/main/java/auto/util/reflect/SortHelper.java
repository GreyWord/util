package auto.util.reflect;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SortHelper {

    /**按第二个List的顺序，获取第一个List的对应字段并排序(如id)(注意id和实体必须唯一对应)
     * <br/>主要用于解决jpa FindByIDin入参和出参顺序不一致的问题*/
    public static <A,B> List<A> sort(List<A> listA,List<B> listB){
        return sort(listA,listB,"id");
    }
    public static <A,B> List<A> sort(List<A> listA,List<B> listB,String field){
        Map<Object, A> map = toMap(listA, field);
        List<A> list = new ArrayList<>();
        listB.forEach(key->list.add(map.get(key)));
        return list;
    }
    public static <A,B> List<A> sort(List<A> listA,List<B> listB,String fieldA,String fieldB){
        return sort(listA,Reflect_tools.getFieldValueList(listB, fieldB),fieldA);
    }

    /**按照指定字段值去重*/
    public static <A> List<A> distinct(List<A> listA,String fieldA){
        return new ArrayList<>(toMap(listA,fieldA).values());
    }
    /**listA的keyA和listB的keyB作为主键对应，把listA中fieldA的值改为listB中fieldB的值*/
    public static <A,B> void change(List<A> listA,List<B> listB,String keyA,String keyB,String fieldA,String fieldB){
        Map<Object, A> aMap = toMap(listA, keyA);
        listB.forEach(obj->
                Reflect_tools.setFieldValue(
                        aMap.get(Reflect_tools.getFieldValue(obj,keyB))
                        ,fieldA
                        ,Reflect_tools.getFieldValue(obj,fieldB)
                )
        );
    }

    public static <A> Map<Object,A> toMap(List<A> list, String field){
        Map<Object,A> map = new LinkedHashMap<>();
        list.forEach(obj->{
            if(obj!=null)
                map.put(Reflect_tools.getFieldValue(obj,field),obj);
        });
        return map;
    }
}
