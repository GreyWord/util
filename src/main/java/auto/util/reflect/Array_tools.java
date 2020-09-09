package auto.util.reflect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.text.MessageFormat.format;

/**
 * @author Created by ZhangXu on 2018/7/3.
 */
public class Array_tools {

    /**
     * 功能：将数组objs以split间隔拼接
     * 作者： 张旭/GreyWord
     * @time 2017-3-13
     */
    public static String ArraytoString(Object[] objs,String split){
        StringBuilder sb = new StringBuilder();
        for(Object obj:objs){
            sb.append(obj);sb.append(split);
        }
        if(sb.length()>0)
            sb.deleteCharAt(sb.length()-split.length());
        return sb.toString();
    }

    /**
     * 功能：从数组objs中依次获取字段值field,并返回
     * 作者： 张旭/GreyWord
     * @time 2017-3-13
     */
    public static List<?> ArraytoArray(List<?> objs, String field){
        final List<Object> list = new ArrayList<>();
        objs.forEach(o -> list.add(Reflect_tools.getFieldValue(o,field)));
        return list;
    }
    /**
     * 功能：从数组objs中依次获取字段值field,并存储至list
     * 作者： 张旭/GreyWord
     * @time 2017-3-13
     */
    public static <T> Collection<T> ArraytoArray(Collection<T> list, List<?> objs, String field){
        objs.forEach(o -> list.add((T) Reflect_tools.getFieldValue(o,field)));
        return list;
    }
    /**
     * 功能：将数组objs以split间隔拼接
     * 作者： 张旭/GreyWord
     * @time 2017-3-13
     */
    public static String ArraytoString(Object[] objs,String parten,String split){
        StringBuilder sb = new StringBuilder();
        for(Object obj:objs){
            sb.append(format(parten,obj));sb.append(split);
        }
        if(sb.length()>0)
            sb.deleteCharAt(sb.length()-split.length());
        return sb.toString();
    }

}
