package auto.util.random;

import java.util.UUID;

/**
 * Created by ZhangXu on 2017/9/13.
 */
public class UUID_tools {
    public static String UUID(){
        String uuid = UUID.randomUUID().toString().replace("-","");
        return uuid;
    }
    public static String UUID(byte length){
        String uuid = UUID().substring(0,length);
        if(length>32)uuid+=UUID((byte) (length-32));
        return uuid;
    }
    public static String UUID(String start){
        StringBuilder sb = new StringBuilder(start.length()+33);
        sb.append(start);
        sb.append(":");
        sb.append(UUID());
        return sb.toString();
    }
}
