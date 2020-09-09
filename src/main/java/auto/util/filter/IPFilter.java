package auto.util.filter;

import auto.util.net.IpUtils;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by ZhangXu on 2017/7/31.
 */
public class IPFilter implements Filter {
    private static ThreadLocal<String> localip = new ThreadLocal<>();
    private static ThreadLocal<HttpServletRequest> localreq = new ThreadLocal<>();
    public void destroy() {
    }

    public void doFilter(javax.servlet.ServletRequest req, javax.servlet.ServletResponse resp, javax.servlet.FilterChain chain) throws javax.servlet.ServletException, IOException {
        before((HttpServletRequest) req);
        chain.doFilter(req, resp);
        after((HttpServletRequest) req);
    }

    public void init(javax.servlet.FilterConfig config) throws javax.servlet.ServletException {

    }
    private static void before(HttpServletRequest request){
        localip.set(IpUtils.getIpAddr(request));
        localreq.set(request);
    }
    private static void after(HttpServletRequest request){
        localip.remove();
        localreq.remove();
    }
    public static String getIp(){
        return localip.get();
    }
    public static HttpServletRequest getReq(){
        return localreq.get();
    }
    public static void setReq(HttpServletRequest request){
        if(localreq.get()==null)
            before(request);
    }
}
