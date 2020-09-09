package auto.util.filter;

import auto.util.net.IpUtils;
import com.alibaba.fastjson.JSON;
import jodd.util.URLDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * @Created by fcj on 2016/12/25.
 *
 */
public class LogsFilter implements Filter {

    private Logger log = LoggerFactory.getLogger(LogsFilter.class);
    private Map<Long,String> map = new HashMap<>();
    Timer timer = new Timer();
    /**日志记录类*/
    private AdminOperateLog adminOperateLog = new AdminOperateLog();


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        timer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {map.clear();}},1000*60*60*12,1000*60*60*12);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        try {
            HttpServletRequest request = (HttpServletRequest)servletRequest;
            //日志map
            Map<String, Object> logMap = new LinkedHashMap<String, Object>();
            String ip = IpUtils.getIpAddr(request);
            logMap.put("ip", ip);
            logMap.put("uri", request.getRequestURI());
            Long numberIp = IpUtils.ip2Long(ip);
            if(numberIp!=0 && !map.containsKey(numberIp)){
                map.put(numberIp,"not found");
                logMap.put("query", request.getQueryString());
                logMap.put("agent", request.getHeader("User-Agent"));
                String info = IpUtils.ip2Address(ip);
                logMap.put("address", info);
                map.put(numberIp, info);
            }
            //输出到日志文件
            adminOperateLog.saveLog(logMap);
        } catch (Exception e) {
            log.error("记录日志出错!", e.fillInStackTrace());
        }finally {
            filterChain.doFilter(servletRequest,servletResponse);
        }
    }

    @Override
    public void destroy() {
        timer.cancel();
    }

    /**
     * 操作日志输入到单独的日志文件
     */
    class AdminOperateLog {
        private Logger log = LoggerFactory.getLogger("iphtml");
        public void saveLog(Map<String, Object> map) {
            //直接输出json格式的日志到日志文件
            log.info("{}", JSON.toJSONString(map));
        }
    }
}