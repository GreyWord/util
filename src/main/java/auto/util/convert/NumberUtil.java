package auto.util.convert;

import java.text.DecimalFormat;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Created by ZhangXu on 2018/3/16.
 */
public class NumberUtil {
    private static DecimalFormat df = new DecimalFormat("#.00");
    private static char[] charSet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    public static Double to2(Double num){
        return Double.parseDouble(df.format(num));
    }
    public static Double add(Number... ns){
        double sum = 0;
        for(Number n :ns){
            if(n!=null)sum+=n.doubleValue();
        }
        return sum;
    }
    public static Double del(Number first,Number... ns){
        if(first==null)return (double)0;
        double sum = first.doubleValue();
        for(Number n :ns){
            if(n!=null)sum-=n.doubleValue();
        }
        return sum;
    }
    public static Long del(Long first,Long... ns){
        if(first==null)return (long)0;
        long sum = first;
        for(Number n :ns){
            if(n!=null)sum-=n.doubleValue();
        }
        return sum;
    }
    public static Double division(Number divisor,Number dividend){
        if(divisor==null || dividend==null)return (double)0;
        if(dividend.doubleValue()==0)return (double)0;
        return divisor.doubleValue()/dividend.doubleValue();
    }
    public static Double multiplication(Number... ns){
        if(ns[0]==null)return (double)0;
        double sum = 1;
        for(Number n :ns){
            if(n!=null)sum*=n.doubleValue();
        }
        return sum;
    }
    public static Double div100(Long num){
        if(num==null)return null;
        return num/100.00;
    }
    public static Long multi100(Double num){
        if(num==null)return null;
        return Math.round(num*100);
    }
    public static String AA26(Number num){
        return AA26(num.longValue(),"");
    }
    public static String AA26(long l,String result){
        if(l>=0){
            result = (char)('A'+l%26)+result;
            return AA26(l/26-1,result);
        }
        return result;
    }
    public static Integer[] parseArray(String[] source){
        Integer[] target = new Integer[source.length];
        for (int i=source.length-1;i>=0;i--)
            target[i]=Integer.parseInt(source[i]);
        return target;
    }
    public static Integer[] parseArray(String source,String split){
        return parseArray(source.split(split));
    }
    public static String to62(long number, int length){
        Stack<Character> stack=new Stack<Character>();
        StringBuilder result=new StringBuilder(0);
        while(number!=0){
            stack.add(charSet[new Long((number-(number/62)*62)).intValue()]);
            number=number/62;
        }
        for(;!stack.isEmpty();){
            result.append(stack.pop());
        }
        int result_length = result.length();
        StringBuilder temp0 = new StringBuilder();
        for(int i = 0; i < length - result_length; i++){
            temp0.append('0');
        }

        return temp0.toString() + result.toString();

    }
}
