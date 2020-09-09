package auto.util.date;

/**
 * @Date: 2018/12/20 16:27
 * @Description:
 */
public class Timmer {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    public static void start(){
        threadLocal.set(System.currentTimeMillis());
    }
    public static long end(){
        Long aLong = threadLocal.get();
        threadLocal.remove();
        return System.currentTimeMillis()-aLong;
    }
}
