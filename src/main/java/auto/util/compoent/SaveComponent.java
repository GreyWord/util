package auto.util.compoent;

import auto.util.convert.SerializeUtils;
import auto.util.filter.IPFilter;
import auto.util.filter.JedisFilter;

import java.nio.charset.Charset;


/**
 * @author Created by ZhangXu on 2018/1/17.
 */
public abstract class SaveComponent<T> {
    final static Charset utf8 = Charset.forName("utf8");

    public abstract void Set(Object key, T value);
    public abstract T Get(Object key);
    public abstract void Remove(Object key);

    public static <V> SaveComponent<V> baseJedis(String prefix){
        return new SaveComponentBaseJedis<>(prefix);
    }
    public static <V> SaveComponent<V> baseSession(String prefix){
        return new SaveComponentBaseSession<>(prefix);
    }
    public static void JedisSSet(String key, String value){
        JedisFilter.getJedis().set(key, value);
    }
    public static String JedisSGet(String key){
        return JedisFilter.getJedis().get(key);
    }
    public static void JedisBSet(byte[] key, byte[] value){
        JedisFilter.getJedis().set(key, value);
    }
    public static void JedisBTmpSet(byte[] key, int seconds, byte[] value){
        JedisFilter.getJedis().setex(key, seconds, value);
    }
    public static byte[] JedisBGet(byte[] key){
        return JedisFilter.getJedis().get(key);
    }
    public static void JedisBRemove(byte[] key){
        JedisFilter.getJedis().del(key);
    }
    public static void JedisOTmpSet(String key, Object value){
        JedisOTmpSet(key,1800,value);
    }
    public static void JedisOTmpSet(String key, int seconds, Object value){
        JedisBTmpSet(key.getBytes(utf8),seconds, SerializeUtils.serialize(value));
    }
    public static Object JedisOGet(String key){
        return SerializeUtils.deserialize(JedisBGet(key.getBytes(utf8)));
    }
    public static void JedisORemove(String key){
        JedisBRemove(key.getBytes(utf8));
    }
    public static void SessionSet(String key, Object value){
        IPFilter.getReq().getSession().setAttribute(key, value);
    }
    public static Object SessionGet(String key){
        return IPFilter.getReq().getSession().getAttribute(key);
    }
    public static void SessionRemove(String key){
        IPFilter.getReq().getSession().removeAttribute(key);
    }
    public static void ReqSet(String key, Object value){
        IPFilter.getReq().setAttribute(key, value);
    }
    public static Object ReqGet(String key){
        return IPFilter.getReq().getAttribute(key);
    }
}
