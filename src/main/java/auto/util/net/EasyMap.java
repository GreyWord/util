package auto.util.net;

import auto.util.convert.JSONObject;

import java.util.HashMap;

/**
 * Created by ZhangXu on 2017/9/13.
 */
public class EasyMap extends HashMap<String,Object> {
    public EasyMap set(String key, Object value){
        super.put(key,value);
        return this;
    }
    public static EasyMap easyMap(String key, Object value){
        return new EasyMap().set(key,value);
    }

    @Override
    public String toString() {
        return JSONObject.toString(this);
    }
}
