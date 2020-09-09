package auto.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ZhangXu on 2017/10/9.
 */
public class String_tools {
    public static String unescape(String source){
        StringBuffer buf = new StringBuffer();
        Matcher m = Pattern.compile("\\\\u([0-9A-Fa-f]{4})").matcher(source);
        while (m.find()) {
            int cp = Integer.parseInt(m.group(1), 16);
            m.appendReplacement(buf, "");
            buf.appendCodePoint(cp);
        }
        m.appendTail(buf);
        return buf.toString();
    }
    public static String escape(String s) {
        StringBuilder sb = new StringBuilder(s.length() * 3);
        for (char c : s.toCharArray()) {
            if (c < 256) {
                sb.append(c);
            } else {
                sb.append("\\u");
                sb.append(Character.forDigit((c >>> 12) & 0xf, 16));
                sb.append(Character.forDigit((c >>> 8) & 0xf, 16));
                sb.append(Character.forDigit((c >>> 4) & 0xf, 16));
                sb.append(Character.forDigit((c) & 0xf, 16));
            }
        }
        return sb.toString();
    }
    public static boolean isEmpty(String s){
        return (s==null || s.equals(""));
    }
    public static boolean notEmpty(String s){
        return !isEmpty(s);
    }
}
