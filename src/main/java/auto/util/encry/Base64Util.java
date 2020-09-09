package auto.util.encry;

import java.nio.charset.Charset;
import java.util.Base64;

/**
 * @author Created by ZhangXu on 2017/11/30.
 */
public class Base64Util {
    public final static Base64.Encoder encoder = Base64.getEncoder();
    public final static Base64.Decoder decoder = Base64.getDecoder();
    public final static Charset utf8=Charset.forName("utf8");
    private final static byte[] emptybytes=new byte[0];

    public static byte[] encode(String source){
        if(source==null)source="";
        return encoder.encode(source.getBytes(utf8));
    }
    public static String encodeBase64String(String source){
        if(source==null)source="";
        return new String(encoder.encode(source.getBytes(utf8)));
    }
    public static String encodeBase64String(byte[] source){
        if(source==null)source=new byte[0];
        return new String(encoder.encode(source));
    }
    public static byte[][] encode(String... sources){
        byte[][] bytes = new byte[sources.length][];
        for(int i=0,length=sources.length;i<length;i++)
            bytes[i]=encode(sources[i]);
        return bytes;
    }
    public static byte[] bytes(String source){
        if(source==null)return emptybytes;
        return source.getBytes(utf8);
    }
    public static byte[][] bytes(String... sources){
        byte[][] byteArray = new byte[sources.length][];
        for(int i=0,length=sources.length;i<length;i++)
            byteArray[i]=bytes(sources[i]);
        return byteArray;
    }
}
