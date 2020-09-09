package auto.util.random;

/**
 * @author Created by ZhangXu on 2018/7/3.
 */
public class RandomUtil {
    public enum RandomType{
        number('0',10)
        ,lower('a',10)
        ,upper('A',10)
        ,special(' ',16)
        ;
        final char startChar;
        final int max;
        RandomType(char startChar, int max) {
            this.startChar = startChar;
            this.max = max;
        }
    }
    private final String[] special = "~!@#$%^&*()_+-={}[]:;<>?,.".split("");
    public static String ramdomNumber(int length){
        return ramdom(length,RandomType.number);
    }
    public static String ramdomChar(int length){
        return ramdom(length,RandomType.lower,RandomType.upper);
    }
    public static String ramdom(int length){
        return ramdom(length,RandomType.values());
    }
    public static String ramdom(int length,RandomType... types){
        length=checkLength(length,1,256);
        int max=0,now;
        for (RandomType type:types){
            max+=type.max;
        }
        StringBuilder sb = new StringBuilder(length);
        while (length-->0){
            now=randomInt(max);
            for (RandomType type:types){
                if(now<type.max){
                    sb.append((char)(type.startChar+now));
                    break;
                }
                else
                    now-=type.max;
            }
        }
        return sb.toString();
    }
    public static int randomInt(int length){
        return (int)(Math.random()*length);
    }
    private static int checkLength(int length,int start,int end){
        return length>end?end:length<start?start:length;
    }
}
