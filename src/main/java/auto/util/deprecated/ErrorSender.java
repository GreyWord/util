package auto.util.deprecated;

import auto.util.date.TimeFormat;
import auto.util.net.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author  Created by ZhangXu on 2017/9/27.
 */
class ErrorSender {

    private static Charset charset = Charset.forName("utf8");
    private static int MAX_CYCLE = 10;
    private static Logger log = LoggerFactory.getLogger("errorSender");
    private static Base64.Encoder encoder = Base64.getEncoder();
    private static ExecutorService executor = new ThreadPoolExecutor(1,1,60, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(32),new ThreadPoolExecutor.AbortPolicy());

    static List<String> iplist = IpUtils.findiplist();

    private static String sender = "18231866795";
    private static String senderfu = "<"+sender+"@126.com>";
    private static String senderfull = "Server"+senderfu;
    private static String receiver = "17346518912";
    private static String receiverfu = "<"+receiver+"@163.com>";
    private static String receiverfull = "enginer"+receiverfu;
    private static String password = "myServerPassWord";

    public static void sendError(Throwable e){
        Map<String,String> args = new LinkedHashMap<>();
        args.put("本机IP为",iplist.toString());
        /*if(IPFilter.getReq()!=null){
            args.put("来源IP为",IPFilter.getIp());
            final HttpServletRequest req = IPFilter.getReq();
            args.put("href",req.getRequestURI());
            args.put("query",req.getQueryString());
            final Enumeration<String> headerNames = req.getHeaderNames();
            while (headerNames.hasMoreElements()){
                final String key = headerNames.nextElement();
                args.put(key,req.getHeader(key));
            }
        }*/
        args.put("错误时间为", TimeFormat.datetimeS());

        executor.execute(()-> SendError(args,e));
    }
    public static void sendFile(File... files){
        executor.execute(()->SendFile(files));
    }

    private static void SendError(Map<String,String> args, Throwable e){

        try(Socket socket = new Socket("smtp.126.com", 25)) {

            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));


            reader.readLine();

            MailSendHelper mailSendHelper = new MailSendHelper(reader,outputStream,charset);
            mailSendHelper.talk("HELO 126");
            mailSendHelper.talk("auth login");
            mailSendHelper.talk(Base64encode(sender));
            String logResult = mailSendHelper.talk(Base64encode(password));
            log.info("errorSend"+logResult);


            mailSendHelper.talk("mail from:" + senderfu);
            mailSendHelper.talk("rcpt to:" + receiverfu);
            mailSendHelper.talk("data");


            mailSendHelper.tell("MIME-Version: 1.0",
                                "Content-Type: text/html; charset=UTF-8",
                                "Content-Transfer-Encoding: base64",
                                "from: "+senderfull,
                                "to: "+receiverfull);
            mailSendHelper.tell(bytes("subject: ",TimeFormat.date()," 服务器报警"));

            mailSendHelper.tell("");

            for(Map.Entry<String,String> entry:args.entrySet()){
                mailSendHelper.tell(Base64encode("<h3>",entry.getKey(),": ",entry.getValue(),"</h3>"));
            }

            {
                Throwable roll = e;
                for(int i=0;i<MAX_CYCLE&&roll!=null;i++){
                    mailSendHelper.tell(Base64encode("<h3>"+roll.toString()+"</h3>"));
                    StackTraceElement[] stackTrace = roll.getStackTrace();
                    for(StackTraceElement element:stackTrace){
                        mailSendHelper.tell(Base64encode(element.toString(),"<br>"));
                    }
                    roll = roll.getCause();
                }
            }

            mailSendHelper.tell(".");
            String sendResult = mailSendHelper.talk("");
            log.info("errorSend"+sendResult);

            mailSendHelper.talk("quit");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private static void SendFile(File... files){

        try(Socket socket = new Socket("smtp.126.com", 25)) {

            OutputStream outputStream = socket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            reader.readLine();

            MailSendHelper mailSendHelper = new MailSendHelper(reader,outputStream,charset);
            mailSendHelper.talk("HELO 126");
            mailSendHelper.talk("auth login");
            mailSendHelper.talk(Base64encode(sender));
            String logResult = mailSendHelper.talk(Base64encode(password));
            log.info("fileSend"+logResult);


            mailSendHelper.talk("mail from:" + senderfu );
            mailSendHelper.talk("rcpt to:" + receiverfu );
            mailSendHelper.talk("data");

            mailSendHelper.tell("from: "+senderfull,
                                "to: "+receiverfull);
            mailSendHelper.tell(bytes("subject: =?UTF-8?B?"),
                                Base64encode(TimeFormat.date()+" 服务器日志"),
                                bytes("?="));

            mailSendHelper.tell(
                    "MIME-Version: 1.0",
                    "Content-Type: multipart/mixed;boundary=\"----=_001_NextPart458781482233_=----\"");

            mailSendHelper.tell("",
                                "------=_001_NextPart458781482233_=----",
                                "Content-Type: multipart/alternative;boundary=\"----=_002_NextPart120063042788_=----\"");

            mailSendHelper.tell("",
                                "------=_002_NextPart120063042788_=----",
                                "Content-Type: text/html; charset=UTF-8",
                                "Content-Transfer-Encoding: base64",
                                "");
            mailSendHelper.tell(Base64encode("<h1>传送了一些文件</h1>"));
            mailSendHelper.tell(Base64encode("<h3>本机IP为:",iplist.toString(),"</h3>"));
            mailSendHelper.tell("------=_002_NextPart120063042788_=------");

            {
                /*if(files!=null) for(File file : files)if(file.exists()){
                    mailSendHelper.tell("",
                                        "------=_001_NextPart458781482233_=----",
                                        "Content-Type: application/octet-stream;name=\""+file.getName()+"\"",
                                        //"Content-Transfer-Encoding: base64",
                                        "Content-Disposition: attachment;filename=\""+file.getName()+"\"",
                                        "");
                    byte[] b = new byte[1024];
                    final FileInputStream fileInputStream = new FileInputStream(file);
                    int i;
                    while((i = fileInputStream.read(b))>-1){
                        mailSendHelper.tellpart(b,0,i);
                    }
                    mailSendHelper.tell("");
                }*/
            }
            mailSendHelper.tell("------=_001_NextPart458781482233_=------");

            mailSendHelper.tell(".");
            String sendResult = mailSendHelper.talk("");
            log.info("fileSend"+sendResult);

            mailSendHelper.talk("quit");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    public static void shutdown(){
        executor.shutdown();
    }
    private static byte[] bytes(String source){
        return source.getBytes(charset);
    }
    private static byte[][] bytes(String... sources){
        byte[][] byteArray = new byte[sources.length][];
        for(int i=0,length=sources.length;i<length;i++)
            byteArray[i]=bytes(sources[i]);
        return byteArray;
    }
    private static byte[] Base64encode(String source){
        if(source==null)source="";
        return encoder.encode(source.getBytes(charset));
    }
    private static byte[][] Base64encode(String... sources){
        byte[][] bytes = new byte[sources.length][];
        for(int i=0,length=sources.length;i<length;i++)
            bytes[i]=Base64encode(sources[i]);
        return bytes;
    }
}

class MailSendHelper{
    BufferedReader reader;
    OutputStream outputStream;
    Charset charset;
    private final static byte[] RN = new byte[]{'\r','\n'};

    public MailSendHelper(BufferedReader reader, OutputStream outputStream, Charset charset) {
        this.reader = reader;
        this.outputStream = outputStream;
        this.charset = charset;
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
    public  void tell(String... infos) throws IOException {
        for(String info:infos)
            tell(info.getBytes(charset));
    }
    public  void tell(byte[] info) throws IOException {
        outputStream.write(info);
        outputStream.write(RN);
    }
    public  void tellend() throws IOException {
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
