package auto.util.date;

import java.util.Date;
import java.util.TimeZone;

/**
 * @Auther: 张旭
 * @Date: 2018/9/25 11:20
 * @Description:
 */
public class TimeCompare {

    public final static long oneMinute = 60 * 1000;
    public final static long oneDay = 24 * 3600 * 1000;
    private final static long zoneOffset = TimeZone.getDefault().getRawOffset();;
    /**
     * 检测是否晚于小时和分钟，不检测秒
     * */
    public static boolean isAfterTime(Date start,Date end){
        return diffTime(start, end)>0;
    }
    /**
     * 同一天
     * */
    public static boolean sameDay(Date start,Date end){
        return diffDay(start, end)==0;
    }
    /**
     * 分钟差是否小于指定值，默认120分钟
     * */
    public static boolean nearTime(Date start, Date end){
        return nearTime(start, end,120);
    }
    public static boolean nearTime(Date start,Date end,int maxDiff){
        int i = diffTime(start, end);
        return Math.abs(i)<maxDiff;
    }
    /**
     * 计算分钟差，不判断日期
     * */
    public static int diffTime(Date start,Date end){
        if(start==null || end==null)return 0;
        return (end.getHours()-start.getHours())*60 + (end.getMinutes()-start.getMinutes());
    }
    /**
     * 计算分钟差，判断日期
     * */
    public static int diffTimeWithDay(Date start,Date end){
        if(start==null || end==null)return 0;
        return (int) ((end.getTime()-start.getTime())/oneMinute);
    }
    /**
     * 计算天数差，不计算时分秒
     * end-start
     * */
    public static int diffDay(Date start,Date end){
        if(start==null || end==null)return 0;
        return (int) ((end.getTime()+zoneOffset)/oneDay-(start.getTime()+zoneOffset)/oneDay);
    }
}
