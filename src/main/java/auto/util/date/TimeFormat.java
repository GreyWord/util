package auto.util.date;

/**
 * @author Created by ZhangXu on 2017/9/29.
 */
public class TimeFormat {
    private static long lasttime;
    private static long count;
    /**
     * 通用的yyyy-MM-dd HH:mm:ss.SSS的格式
     */
    public static String now(String source,long l){
        return String.format(FormatType.convert(source),l);
    }
    /**
     * %tF: yyyy-MM-dd<br/>
     * %tT: HH:mm:ss<br/>
     * %tH: HH<br/>
     * %tM: mm<br/>
     * %tS: ss<br/>
     * %tL: SSS<br/>
     * %tY: yyyy<br/>
     * %tm: MM<br/>
     * %td: dd<br/>
     */
    public static String javaFormat(FormatType type){
        return format(type.pattern,System.currentTimeMillis());
    }
    public static String format(FormatType type,long l){
        return format(type.pattern,l);
    }
    public static String format(String pattern,long l){
        return String.format(pattern,l);
    }
    public static String date(){
        return javaFormat(FormatType.date);
    }
    public static String datetime(){
        return javaFormat(FormatType.datetime);
    }
    public static String datetimeS(){
        return javaFormat(FormatType.datetimes);
    }
    public static String time(){
        return javaFormat(FormatType.time);
    }
    public static String timeS(){
        return javaFormat(FormatType.times);
    }
    public static synchronized String uniquetime(){
        long l = System.currentTimeMillis();
        if(l!=lasttime){
            count=0;
            lasttime=l;
        }
        count++;
        return String.format("%d%3d",lasttime,count);
    }
    public enum FormatType{
        date("%tF"),datetime("%tF %<tT"),datetimes("%tF %<tT.%<tL"),time("%tT"),times("%tT.%<tL");
        private final String pattern;
        FormatType(String pattern) {
            this.pattern = pattern;
        }
        public static String convert(String source){
            return source.replace("yyyy","%1$tY")
                    .replace("MM","%1$tm")
                    .replace("dd","%1$td")
                    .replace("HH","%1$tH")
                    .replace("mm","%1$tM")
                    .replace("ss","%1$tS")
                    .replace("SSS","%1$tL")
                    ;
        }
    }
}
