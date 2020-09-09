package auto.util.net;

import auto.util.encry.Encryption;
import auto.util.date.TimeFormat;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.MessageDigest;
import java.util.function.Function;

import static auto.util.encry.Encryption.md5;

/**
 * Created by ZhangXu on 2017/9/27.
 */
public class FtpSender {

    private static Logger log = LoggerFactory.getLogger(FtpSender.class);

    private static String host;
    private static String name;
    private static String passwd;
    private static String encoding;
    private static String imgurl;
    private static String prefix;

    public static String SendDirectoryFile(String fileName, InputStream file,String... Directorys) throws IOException {
        return  FtpSender.useFTP(prefix,(ftp) -> {
            StringBuilder sb = new StringBuilder(imgurl);
            try {
                ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
                toDirectory(ftp,Directorys);
                sb.append(ftp.printWorkingDirectory());
                sb.append("/");
                sb.append(fileName);
                ftp.storeFile(fileName, file);
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        });
    }
    public static boolean existsDirectoryFile(String fileName,String... Directorys) throws IOException {
        return FtpSender.useFTP(prefix,(ftp) -> {
            try {
                toDirectory(ftp,Directorys);
                return ftp.mdtm(fileName)==213;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        });
    }
    public static boolean deleteDirectoryFile(String fileName,String... Directorys) throws IOException {
        return FtpSender.useFTP(prefix,(ftp) -> {
            try {
                toDirectory(ftp,Directorys);
                return ftp.deleteFile(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        });
    }
    public static String getDirectoryFilePath(String fileName,String... Directorys) throws IOException {
        return FtpSender.useFTP(prefix,(ftp) -> {
            StringBuilder sb = new StringBuilder(imgurl);
            try {
                toDirectory(ftp,Directorys);
                sb.append(ftp.printWorkingDirectory());
                sb.append("/");
                sb.append(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        });
    }
    public static void getUrlFile(String url, OutputStream file) throws IOException {
        String[] directoryFiles = url.replace(imgurl, "").split("/");
        FtpSender.useFTP(prefix,(ftp) -> {
            try {
                int j=directoryFiles.length-1;
                for (int i = 0;i<j;i++){
                    ftp.changeWorkingDirectory(directoryFiles[i]);
                }
                String realName = directoryFiles[j];
                if(ftp.mdtm(realName)==213){
                    ftp.retrieveFile(realName,file);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 1;
        });
    }
    private static void toDirectory(FTPClient ftp,String... Directorys) throws IOException {
        if (Directorys != null)
            for (String Directory : Directorys) {
                ftp.makeDirectory(Directory);
                ftp.changeWorkingDirectory(Directory);
            }
    }
    public static String SendMD5File(String fileName, InputStream file) throws IOException {
        return SendMD5File(fileName,file,1024);
    }
    public static String SendMD5File(String fileName, InputStream file, int maxK) throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(host);
        ftpClient.login(name,passwd);
        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
        final File tempFile = File.createTempFile(TimeFormat.uniquetime(), ".tmp");
        final RandomAccessFile out = new RandomAccessFile(tempFile,"rw");
        final MessageDigest instance = md5.getInstance();
        byte[] b = new byte[1024];
        int i;
        while((i=file.read(b))!=-1){
            instance.update(b,0,i);
            out.write(b, 0, i);
            if(maxK--<0)break;
        }
        String suffix = fileName.substring(fileName.lastIndexOf('.'));
        if("".equals(suffix))suffix=".obj";
        String fileSize = out.getFilePointer()+"";
        String md5 = Encryption.toString(instance);
        String realName=md5+suffix;
        out.close();
        ftpClient.changeWorkingDirectory("second");
        changeDirectory(ftpClient,suffix);
        changeDirectory(ftpClient,fileSize);
        String pwd = ftpClient.printWorkingDirectory();
        if(ftpClient.mdtm(realName)!=213){
            final FileInputStream in = new FileInputStream(tempFile);
            ftpClient.storeFile(realName,in);
            in.close();
        }
        tempFile.delete();
        StringBuilder sb = new StringBuilder(imgurl);
        sb.append(pwd);
        sb.append("/");
        sb.append(realName);
        ftpClient.logout();
        ftpClient.disconnect();
        return sb.toString();
    }
    public static String existsMD5File(String suffix,String md5, String fileSize) throws IOException {
        suffix = suffix.substring(suffix.indexOf("."));
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(host);
        ftpClient.login(name,passwd);
        ftpClient.changeWorkingDirectory("second");
        String realName=md5+suffix;
        String result=null;
        if( ftpClient.changeWorkingDirectory(suffix) &&
                ftpClient.changeWorkingDirectory(fileSize) &&
                ftpClient.mdtm(realName)==213){
            StringBuilder sb = new StringBuilder(imgurl);
            sb.append("/second/");
            sb.append(suffix);
            sb.append("/");
            sb.append(fileSize);
            sb.append("/");
            sb.append(realName);
            result=sb.toString();
        }
        ftpClient.logout();
        ftpClient.disconnect();
        return result;
    }
    private static <T> T useFTP(String directory, Function<FTPClient,T> useClient) throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setControlEncoding(encoding);
        ftpClient.connect(host);
        ftpClient.login(name,passwd);
        ftpClient.changeWorkingDirectory(directory);
        final T apply = useClient.apply(ftpClient);
        ftpClient.logout();
        ftpClient.disconnect();
        return apply;
    }
    public static void setconf(String host,String name,String passwd,String encoding,String imgurl,String prefix){
        FtpSender.host=host;
        FtpSender.passwd=passwd;
        FtpSender.name=name;
        FtpSender.encoding=encoding;
        FtpSender.imgurl=imgurl;
        FtpSender.prefix=prefix;
    }
    private static boolean changeDirectory(FTPClient ftpClient,String directory) throws IOException {
        if(ftpClient.changeWorkingDirectory(directory)){
            return true;
        }else {
            ftpClient.makeDirectory(directory);
            return ftpClient.changeWorkingDirectory(directory);
        }
    }
}
