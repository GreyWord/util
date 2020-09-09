package auto.util.filter;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.*;
import java.io.IOException;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @Created by fcj on 2016/12/25.
 *
 */
public class JedisFilter implements Filter {

    static JedisPool jedisPool;
    private static ThreadLocal<Jedis> pool = new ThreadLocal<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final Jedis jedis = jedisPool.getResource();
        pool.set(jedis);
        filterChain.doFilter(servletRequest,servletResponse);
        pool.remove();
        jedis.close();
    }

    @Override
    public void destroy() {

    }

    public static void setJedisPool(JedisPool jedisPool) {
        JedisFilter.jedisPool = jedisPool;
    }
    public static Jedis getJedis(){
        return pool.get();
    }
    public static <T> T useJedis(Function<Jedis,T> exec){
        final Jedis jedis = jedisPool.getResource();
        final T apply = exec.apply(jedis);
        jedis.close();
        return apply;
    }
    public static <T> T whenJedis(Supplier<T> exec){
        final Jedis jedis = pool.get();
        if(jedis==null)return null;
        return exec.get();
    }
    public static void whenJedis(Runnable exec){
        final Jedis jedis = pool.get();
        if(jedis==null)return ;
        exec.run();
    }
}