package auto.util;

import java.util.HashMap;

/**
 * Created by ZhangXu on 2017/9/13.
 * put方法是默认无效的,但putAll方法没有重写
 */
public class ResponseMap extends HashMap<String,Object> {
    public ResponseMap setObject(String key,Object value){
        super.put(key,value);
        return this;
    }
    public ResponseMap setErr(Exception e){
        return this.setErr(e,300);
    }
    protected ResponseMap setCode(Integer code){
        super.put("code",code);
        return this;
    }
    public Integer getCode(){
        return (Integer) this.get("code");
    }
    public ResponseMap setErr(Exception e,Integer code){
        return this.setErr(e.getMessage(),code);
    }
    public ResponseMap setErr(String msg,Integer code){
        this.clear();
        super.put("code",code);
        super.put("status","error");
        super.put("msg",msg);
        return this;
    }
    public static ResponseMap newSuccess(String key,Object value){
        return new ResponseMap().setObject(key,value);
    }
    public static ResponseMap newErr(String msg,Integer code){
        return new ResponseMap().setErr(msg,code);
    }
    public ResponseMap() {
        this.setCode(200);
        super.put("status","success");
    }

    @Override
    public Object put(String key, Object value) {
        return null;
    }
}
