package auto.util.convert;

import org.nustaq.serialization.FSTConfiguration;

import java.io.*;

/**
 * @author Created by ZhangXu on 2017/11/24.
 */
public class SerializeUtils {
    public enum SerializeType{
        Default(FSTConfiguration.createDefaultConfiguration())
        ,UnsafeBinary(FSTConfiguration.createUnsafeBinaryConfiguration())
        ,Json(FSTConfiguration.createJsonConfiguration())
        ,Struct(FSTConfiguration.createStructConfiguration())
        ,MinBin(FSTConfiguration.createMinBinConfiguration())
        ;
        final FSTConfiguration configuration;
        SerializeType(FSTConfiguration configuration) {
            configuration.setClassLoader(SerializeUtils.class.getClassLoader());
            this.configuration = configuration;
        }
        public byte[] serialize(Object obj) {
            return configuration.asByteArray(obj);
        }
        public Object deserialize(byte[] sec) {
            return configuration.asObject(sec);
        }
    }
    private static FSTConfiguration configuration = SerializeType.Default.configuration;

    public static byte[] serialize(Object obj) {
        if(obj==null)return new byte[0];
        return configuration.asByteArray(obj);
    }
    public static Object deserialize(byte[] sec) {
        if(sec==null || sec.length==0)return null;
        return configuration.asObject(sec);
    }
    public static <T> T deserialize(byte[] sec,Class<T> tClass) {
        if(sec==null || sec.length==0)return null;
        return tClass.cast(configuration.asObject(sec));
    }
    public static byte[] serialize(Object obj,SerializeType type) {
        if(obj==null)return new byte[0];
        return type.serialize(obj);
    }
    public static Object deserialize(byte[] sec,SerializeType type) {
        if(sec==null || sec.length==0)return null;
        return type.deserialize(sec);
    }
    // jdk原生序列换方案
    public static byte[] jdkserialize(Object obj) {
        if(obj==null)return new byte[0];
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos);) {
            oos.writeObject(obj);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object jdkdeserialize(byte[] bits) {
        if(bits==null || bits.length==0)return null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bits);
             ObjectInputStream ois = new ObjectInputStream(bais);
        ) {
            return ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
