package auto.util.net;

import auto.util.filter.IPFilter;

import javax.servlet.http.Cookie;
import java.util.Objects;

/**
 * @author Created by ZhangXu on 2017/9/14.
 */
public class Cookie_tools {
    private static String path = "/";
    private static int expir = 60 * 3600;
    /**
     * 返回对应cookie值,不存在时返回""
     */
    public static String getValue(String name){
        Cookie[] cookies = IPFilter.getReq().getCookies();
        if(cookies!=null)
            for (Cookie cookie:cookies) {
                if(Objects.equals(name,cookie.getName()))
                    return cookie.getValue();
            }
        return "";
    }
    public static Cookie create(String name, String value){
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(path);
        cookie.setMaxAge(expir);
        return cookie;
    }
    public static Cookie create(String name, String value, String path){
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(path);
        cookie.setMaxAge(expir);
        return cookie;
    }
    public static Cookie create(String name, String value, String path,int expir){
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(path);
        cookie.setMaxAge(expir);
        return cookie;
    }
}
