package auto.util.net;

import auto.util.encry.Base64Util;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * @author Created by ZhangXu on 2017/11/30.
 */
public class MailSend{
    String name,account,passwd,address;
    String target,targetName;
    private NeedDo beferStart;
    private ManulControl inHeader;
    private ManulControl whenContent;
    private String subject;
    private String content;
    private NeedDo afterEnd;
    private File[] files;
    private static Charset charset=Charset.forName("utf8");
    public MailSend(String name, String account, String passwd, String address) {
        this.name = name;
        this.account = account;
        this.passwd = passwd;
        this.address = address;
    }
    public void setTarget(String target,String targetName){
            this.target=target;
            this.targetName=targetName;
    }
    public void send(){
        if(beferStart!=null)beferStart.run();
        try(Socket socket = new Socket("smtp."+address, 25)) {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            reader.readLine();
            MailSendHelper mailSendHelper = new MailSendHelper(reader,outputStream,charset);
            mailSendHelper.talk("HELO 126");
            mailSendHelper.talk("auth login");
            mailSendHelper.talk(Base64Util.encode(account));
            mailSendHelper.talk(Base64Util.encode(passwd));

            mailSendHelper.talk("mail from:<",account,"@",address,">");
            mailSendHelper.talk("rcpt to:<" , target,">");
            mailSendHelper.talk("data");
            mailSendHelper.tell(String.format("from: %s<%s@%s>",name,account,address),
                    String.format("to: %s<%s>",targetName,target));
            if(files==null){
                mailSendHelper.tell("MIME-Version: 1.0",
                        "Content-Type: text/html; charset=UTF-8",
                        "Content-Transfer-Encoding: base64");
                if(subject!=null)mailSendHelper.tell(Base64Util.bytes("subject: ",subject));
                if(inHeader!=null)inHeader.run(mailSendHelper);
                mailSendHelper.tell("");
                if(whenContent!=null)whenContent.run(mailSendHelper);
                if(content!=null)mailSendHelper.tell(content);
                mailSendHelper.flush();
            }else{
                mailSendHelper.tell(Base64Util.bytes("subject: =?UTF-8?B?"),
                        Base64Util.encode(subject),
                        Base64Util.bytes("?="));
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
                if(whenContent!=null)whenContent.run(mailSendHelper);
                if(content!=null)mailSendHelper.tell(content);
                mailSendHelper.tell("------=_002_NextPart120063042788_=------");
                {
                    for(File file : files)if(file.exists()){
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
                    }
                }
                mailSendHelper.tell("------=_001_NextPart458781482233_=------");
                mailSendHelper.flush();
            }
            mailSendHelper.tell(".");
            mailSendHelper.talk("");
            mailSendHelper.talk("quit");
            if(afterEnd!=null)afterEnd.run();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    public void setBeferStart(NeedDo beferStart) {
        this.beferStart = beferStart;
    }
    public void setSubject(String subject){
        this.subject=subject;
    }
    public void setInHeader(ManulControl inHeader){
        this.inHeader=inHeader;
    }
    public void setWhenContent(ManulControl afterHeader){
        this.whenContent=afterHeader;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setFiles(File... files) {
        this.files = files;
    }
    public void setAfterEnd(NeedDo afterEnd) {
        this.afterEnd = afterEnd;
    }
    public interface ManulControl {
        void run(MailSendHelper mailSendHelper) throws IOException;
    }
    public interface NeedDo{
        void run();
    }
}
