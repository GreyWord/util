package auto.util.net;

import auto.util.encry.Base64Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * @author Created by ZhangXu on 2017/11/30.
 */
public class MailSendHelper{
    BufferedReader reader;
    OutputStream outputStream;
    Charset charset;
    private final static byte[] RN = new byte[]{'\r','\n'};

    public MailSendHelper(BufferedReader reader, OutputStream outputStream, Charset charset) {
        this.reader = reader;
        this.outputStream = outputStream;
        this.charset = charset;
    }
    public  String talk(String... infos) throws IOException {
        final int index = infos.length-1;
        for(int i=0;i<index;i++)outputStream.write(infos[i].getBytes(charset));
        return talk(infos[index].getBytes(charset));
    }
    public  String talk(String info) throws IOException {
        return talk(info.getBytes(charset));
    }
    public  String talk(byte[] info) throws IOException {
        outputStream.write(info);
        outputStream.write(RN);
        outputStream.flush();
        return reader.readLine();
    }
    /** tell(String...)与tell(bytes...)的行为不完全一致<br>
     * tell(String...)与tell(bytes)的行为一致<br>*/
    public  void tell(byte[]... infos) throws IOException {
        for (byte[] info:infos)
            outputStream.write(info);
        outputStream.write(RN);
    }
    public void writeLine(String... contents) throws IOException {
            tell(Base64Util.encode(contents));
    }
    public  void tell(String... infos) throws IOException {
        for(String info:infos)
            tell(info.getBytes(charset));
    }
    public  void tell(byte[] info) throws IOException {
        outputStream.write(info);
        outputStream.write(RN);
    }
    public  void flush() throws IOException {
        outputStream.flush();
    }

    public  void tellpart(byte[]... infos) throws IOException {
        for(byte[] info:infos)
            outputStream.write(info);
    }
    public  void tellpart(byte[] info, int off, int len) throws IOException {
        outputStream.write(info,off,len);
    }

}
