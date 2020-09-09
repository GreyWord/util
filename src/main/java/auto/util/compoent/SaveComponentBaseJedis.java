package auto.util.compoent;

/**
 * @author Created by ZhangXu on 2018/7/10.
 */
class SaveComponentBaseJedis<T> extends SaveComponent<T>{

    private final String prefix;

    SaveComponentBaseJedis(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void Set(Object key, T value) {
        SaveComponent.JedisOTmpSet(prefix+key,600,value);
    }

    @Override
    public T Get(Object key) {
        return (T)SaveComponent.JedisOGet(prefix+key);
    }

    @Override
    public void Remove(Object key) {
        SaveComponent.JedisORemove(prefix+key);
    }
}
