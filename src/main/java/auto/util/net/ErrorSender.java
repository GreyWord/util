package auto.util.net;

import auto.util.date.TimeFormat;
import auto.util.filter.IPFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by ZhangXu on 2017/9/27.
 */
public class ErrorSender {

    private static int MAX_CYCLE = 10;
    private static Logger log = LoggerFactory.getLogger("errorSender");
    private static ExecutorService executor = new ThreadPoolExecutor(1,1,60, TimeUnit.SECONDS,new ArrayBlockingQueue<>(32),new ThreadPoolExecutor.AbortPolicy());

    static List<String> iplist = IpUtils.findiplist();

    private static String name = "Server";
    private static String account = "18231866795";
    private static String password = "myServerPassWord";
    private static String address = "126.com";
    private static String receiver = "17346518912@163.com";
    private static String receiverName = "engine";

    public static void sendError(Throwable e){
        Map<String,String> args = new LinkedHashMap<>();
        sendError(e,args);
    }
    public static void sendError(Throwable e,Map<String,String> map){
        Map<String,String> args = new LinkedHashMap<>();
        if(map!=null)args.putAll(map);
        args.put("本机IP为",iplist.toString());
        if(IPFilter.getReq()!=null){
            args.put("来源IP为",IPFilter.getIp());
            final HttpServletRequest req = IPFilter.getReq();
            args.put("href",req.getRequestURI());
            args.put("query",req.getQueryString());
            final Enumeration<String> headerNames = req.getHeaderNames();
            while (headerNames.hasMoreElements()){
                final String key = headerNames.nextElement();
                args.put(key,req.getHeader(key));
            }
        }
        executor.execute(()-> SendError(args,e));
    }

    private static void SendError(Map<String,String> args, Throwable e){
        MailSend mailSend = new MailSend(name,account,password,address);
        mailSend.setTarget(receiver,receiverName);
        mailSend.setSubject(TimeFormat.date()+" 服务器报警");
        mailSend.setWhenContent((mailSendHelper)->{
            for(Map.Entry<String,String> entry:args.entrySet()){
                mailSendHelper.writeLine("<h3>",entry.getKey(),": ",entry.getValue(),"</h3>");
            }
            {
                Throwable roll = e;
                for(int i=0;i<MAX_CYCLE&&roll!=null;i++){
                    mailSendHelper.writeLine("<h3>",roll.toString(),"</h3>");
                    StackTraceElement[] stackTrace = roll.getStackTrace();
                    for(StackTraceElement element:stackTrace){
                        mailSendHelper.writeLine(element.toString(),"<br>");
                    }
                    roll = roll.getCause();
                }
            }
        });
        mailSend.setAfterEnd(()->{
            log.info("errorSend complate");
        });
        mailSend.send();
    }
    public static void sendFile(File... files){
        executor.execute(()->SendFile(files));
    }
    private static void SendFile(File... files){
        MailSend mailSend = new MailSend(name,account,password,address);
        mailSend.setTarget(receiver,receiverName);
        mailSend.setSubject(TimeFormat.date()+" 服务器日志");
        mailSend.setFiles(files);
        mailSend.setWhenContent((mailSendHelper)->{
            mailSendHelper.writeLine("<h1>传送了一些文件</h1>");
            mailSendHelper.writeLine("<h3>本机IP为:",iplist.toString(),"</h3>");
        });
        mailSend.setAfterEnd(()->{
            log.info("fileSend success");
        });
        mailSend.send();
    }
    public static void shutdown(){
        executor.shutdown();
    }
}

