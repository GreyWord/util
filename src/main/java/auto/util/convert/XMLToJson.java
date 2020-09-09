package auto.util.convert;

import com.alibaba.fastjson.JSONObject;
import org.dom4j.*;

import java.util.*;

/**
 * @author Created by ZhangXu on 2018/3/20.
 */
public class XMLToJson {

    public static String xml2json(String xmlString){
        final Map<String,Object> map = xml2map(xmlString);
        return JSONObject.toJSONString(map);
    }

    public static Map<String, Object> xml2map(String xmlString) {
        Document doc;
        try {
            doc = DocumentHelper.parseText(xmlString);
        } catch (DocumentException e) {
            return new HashMap<>();
        }
        Element rootElement = doc.getRootElement();
        final Map<String, Object> map = Dom2Map(rootElement);
        return map;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> Dom2Map(Element element){
        Map<String, Object> map = new LinkedHashMap<>();
        element.elements().forEach((el)->{
            Element e = (Element)el;
            if(e.attributes().size()>0 && e.isTextOnly()){
                Map<String,String> values = new LinkedHashMap<>();
                e.attributes().forEach(att->{
                    Attribute t = (Attribute)att;
                    values.put(t.getName(),t.getValue());
                });
                put(map,e.getName(),values);
            } else if(e.isTextOnly()){
                put(map,e.getName(),e.getStringValue());
            } else{
                put(map,e.getName(),Dom2Map(e));
            }
        });
        return map;
    }
    @SuppressWarnings("unchecked")
    private static void put(Map<String, Object> map,String key,Object value){
        final Object o = map.get(key);
        if(o==null)
            map.put(key,value);
        else if(o instanceof List)
            ((List) o).add(value);
        else {
            List<Object> list = new ArrayList<>();
            list.add(o);
            list.add(value);
            map.put(key,list);
        }
    }

}
