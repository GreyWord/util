package auto.util.date;

import java.util.Calendar;
import java.util.Date;

/**
 * @Auther: 张旭
 * @Date: 2018/9/20 08:48
 * @Description:
 */
public class Date_tools {
    public enum DayType{
        YearStart,MonthStart,DayStart,HourStart,MinuteStart,SecondStart
    }
    /**指定年月日的时间*/
    public static Calendar dayTime(Calendar cale,Integer year,Integer month,Integer day){
        return dayTime(cale, year, month, day,0,0,0);
    }
    /**指定年月日时分秒的时间*/
    public static Calendar dayTime(Calendar cale,Integer year,Integer month,Integer day, Integer hour,Integer minute,Integer second){
        cale = cale==null?Calendar.getInstance():cale;
        if(year!=null)  cale.set(Calendar.YEAR, year);
        if(month!=null) cale.set(Calendar.MONTH, month-1);
        if(day!=null)   cale.set(Calendar.DAY_OF_MONTH, day);
        if(hour!=null)  cale.set(Calendar.HOUR_OF_DAY, hour);
        if(minute!=null)cale.set(Calendar.MINUTE, minute);
        if(second!=null)cale.set(Calendar.SECOND, second);
        cale.set(Calendar.MILLISECOND, 0);
        return cale;
    }
    /**
     * 滚动指定的天数
     * */
    public static Calendar rollDay(Calendar start,double roll){
        Calendar cale = Calendar.getInstance();
        cale.setTime(rollDay(start==null?null:start.getTime(),roll));
        return cale;
    }
    public static Date rollDay(Date start,double roll){
        long base;
        if(start==null)base=System.currentTimeMillis();
        else base=start.getTime();
        return new Date(base+(long)(TimeCompare.oneDay*roll));
    }
    /**滚动年月日的时间*/
    public static Calendar rollDay(Calendar cale,Integer year,Integer month,Integer day){
        return rollDayTime(cale,year,month,day,0,0,0);
    }
    /**滚动时分秒的时间*/
    public static Calendar rollTime(Calendar cale, Integer hour,Integer minute,Integer second){
        return rollDayTime(cale,0,0,0,hour,minute,second);
    }
    /**滚动年月日时分秒的时间*/
    public static Calendar rollDayTime(Calendar cale,Integer year,Integer month,Integer day, Integer hour,Integer minute,Integer second){
        cale = cale==null?Calendar.getInstance():cale;
        if(year!=null)  cale.roll(Calendar.YEAR, year);
        if(month!=null) cale.roll(Calendar.MONTH, month);
        if(day!=null)   cale.roll(Calendar.DAY_OF_MONTH, day);
        if(hour!=null)  cale.roll(Calendar.HOUR_OF_DAY, hour);
        if(minute!=null)cale.roll(Calendar.MINUTE, minute);
        if(second!=null)cale.roll(Calendar.SECOND, second);
        return cale;
    }
    /**把时间转换成Calendar*/
    public static Calendar toCalendar(long millis){
        Calendar cale = Calendar.getInstance();
        cale.setTimeInMillis(millis);
        return cale;
    }
    /**把时间转换成Calendar*/
    public static Calendar toCalendar(Date date){
        Calendar cale = Calendar.getInstance();
        cale.setTime(date);
        return cale;
    }
    /**获取指定类型的时间*/
    public static Calendar getDayTime(DayType type){
        switch (type){
            case YearStart:
                return dayTime(null, null, 1, 1);
            case MonthStart:
                return dayTime(null, null, null, 1);
            default:
            case DayStart:
                return dayTime(null, null, null, null);
            case HourStart:
                return dayTime(null, null, null, null,null,0,0);
            case MinuteStart:
                return dayTime(null, null, null, null,null,null,0);
            case SecondStart:
                return dayTime(null, null, null, null,null,null,null);
        }
    }
}
