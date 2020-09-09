package auto.util.encry;

import auto.util.convert.NumberUtil;

import java.io.UnsupportedEncodingException;
import java.util.Stack;

/**
 * @author Created by ZhangXu on 2018/7/10.
 */
public class URLEncoder {
    public final static String utf8="utf8";
    public final static String gbk="gbk";
    public static String encodeUTF8(String dest){
        return encode(dest,utf8);
    }
    public static String encodeGBK(String dest){
        return encode(dest,gbk);
    }
    public static String encode(String dest,String charset){
        try {
            return java.net.URLEncoder.encode(dest,charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
    //短链接生成算法，基于md5
    public static String toShort(String url){
        String md5 = Encryption.md5.encrypte(url);
        long a = Long.parseLong(md5.substring(0,8),16)*2;
        long b = Long.parseLong(md5.substring(8,16),16)*2;
        long c = Long.parseLong(md5.substring(16,24),16)*3;
        long d = Long.parseLong(md5.substring(24,32),16)*4;
        long e = a+b+c+d;//倍数最高13
        return NumberUtil.to62(e, 6);
    }
}
