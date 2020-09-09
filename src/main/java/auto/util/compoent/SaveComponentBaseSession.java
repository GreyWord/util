package auto.util.compoent;

/**
 * @author Created by ZhangXu on 2018/7/10.
 */
class SaveComponentBaseSession<T> extends SaveComponent<T>{

    private final String prefix;

    SaveComponentBaseSession(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void Set(Object key, T value) {
        SaveComponent.SessionSet(prefix+key,value);
    }

    @Override
    public T Get(Object key) {
        return (T)SaveComponent.SessionGet(prefix+key);
    }

    @Override
    public void Remove(Object key) {
        SaveComponent.SessionRemove(prefix+key);
    }
}
