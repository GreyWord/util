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
    public static String format(String source,long l){
        return String.format(DateType.convert(source),l);
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
    public static String now(DateType type){
        return jFormat(type.pattern,System.currentTimeMillis());
    }
    public static String jFormat(DateType type, long l){
        return jFormat(type.pattern,l);
    }
    public static String jFormat(String pattern, long l){
        return String.format(pattern,l);
    }
    public static String date(){
        return now(DateType.date);
    }
    public static String datetime(){
        return now(DateType.datetime);
    }
    public static String datetimeS(){
        return now(DateType.datetimes);
    }
    public static String time(){
        return now(DateType.time);
    }
    public static String timeS(){
        return now(DateType.times);
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
    public enum DateType {
        date("%tF"),datetime("%tF %<tT"),datetimes("%tF %<tT.%<tL"),time("%tT"),times("%tT.%<tL");
        private final String pattern;
        DateType(String pattern) {
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
