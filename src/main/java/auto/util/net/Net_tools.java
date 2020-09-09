package auto.util.net;

import auto.util.encry.Base64Util;
import auto.util.encry.URLEncoder;
import auto.util.filter.IPFilter;
import auto.util.io.IO_tools;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by ZhangXu on 2017/7/31.
 */
public class Net_tools {
	public enum SendType{
		xml("application/xml;charset=UTF-8")
		,json("application/json;charset=UTF-8")
		,form("application/x-www-form-urlencoded")
		,text("text/html;charset=UTF-8");
		final String ContentType;
		SendType(String ContentType) {
			this.ContentType=ContentType;
		}
	}
	public static class whenSend{
		OutputStream out;
		private whenSend(OutputStream out) {
			this.out = out;
		}
		public void send(String content){
            try {
                if(content.length()<1024){
                    out.write(content.getBytes(Base64Util.utf8));
                }else {
                    ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes("utf8"));
                    IO_tools.fromInToOut(in, out);
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
	public static <T> T testSuccess(String url, Function<InputStream,T> whenSuccess) throws IOException{
			return test(url,2000,whenSuccess,null);
	}
	public static <T> T testError(String url, Supplier<T> whenError) throws IOException{
		return test(url,2000,null,whenError);
	}
	public static <T> T test(String url,int timeout, Function<InputStream,T> whenSuccess, Supplier<T> whenError) throws IOException{
		URL myURL = new URL(url);
		URLConnection httpsConn = myURL.openConnection();
		httpsConn.setConnectTimeout(timeout);
		try(InputStream in = httpsConn.getInputStream()){
			return whenSuccess.apply(in);
		}catch (java.net.SocketTimeoutException e){
			return whenError.get();
		}
	}
	public static String get(String url) throws IOException{
		return get(url, Collections.emptyMap());
	}
	public static void get(String url,OutputStream out) throws IOException{
		URL myURL = new URL(url);
		URLConnection httpsConn = myURL.openConnection();
		BufferedInputStream read = new BufferedInputStream(httpsConn.getInputStream());
		IO_tools.fromInToOut(read,out);
		read.close();
		out.close();
	}
	public static String get(String url,Map<String,String> headers) throws IOException{
		URL myURL = new URL(url);
		URLConnection httpsConn = myURL.openConnection();
		headers.forEach(httpsConn::addRequestProperty);
		BufferedInputStream read = new BufferedInputStream(httpsConn.getInputStream());
		StringBuilder sb = IO_tools.fromInToOut(read,new StringBuilder());
		read.close();
		return sb.toString();
	}
	public static String getWithCookie(String url,Map<String,String> headers) throws IOException{
		URL myURL = new URL(url);
		URLConnection httpsConn = myURL.openConnection();
		headers.forEach(httpsConn::addRequestProperty);
		BufferedInputStream read = new BufferedInputStream(httpsConn.getInputStream());
		Map<String, List<String>> fields = httpsConn.getHeaderFields();
		StringBuilder sb2 = new StringBuilder();
		fields.forEach((field,values)->{
			if(!"Set-Cookie".equals(field))return;
			values.forEach((cookie)->{
				String[] split = cookie.split(";");
				sb2.append(split[0]);sb2.append(";");
			});
		});
		headers.put("Cookie", sb2.toString());
		StringBuilder sb = IO_tools.fromInToOut(read,new StringBuilder());
		read.close();

		return sb.toString();
	}
	public static String forward(String url) throws IOException{
		URL myURL = new URL(url);
		URLConnection httpsConn = myURL.openConnection();
		httpsConn.setRequestProperty("X-Forwarded-For", IPFilter.getIp());
		httpsConn.setRequestProperty("User-Agent", IPFilter.getReq().getHeader("User-Agent"));
		BufferedInputStream read = new BufferedInputStream(httpsConn.getInputStream());
		StringBuilder sb = IO_tools.fromInToOut(read,new StringBuilder());
		read.close();
		return sb.toString();
	}
	public static String post(String url,String json) throws IOException{
		return post(url, json, SendType.json);
	}
	public static String post(String url,String text,SendType type) throws IOException{
        return post(url, (send -> send.send(text)), type);
	}
    public static String post(String url, List<String> text, SendType type) throws IOException{
        return post(url, (send -> text.forEach(send::send)), type);
    }
    public static String post(String url, List<String> text,String split, SendType type) throws IOException{
        return post(url, (send -> text.forEach(s -> {send.send(s);send.send(split);}) ), type);
    }
	public static String postform(String url, Map<String,String> map) throws IOException{
		return post(url, (send -> map.forEach((key,value) -> {
			send.send(URLEncoder.encodeUTF8(key));
			send.send("=");
			send.send(URLEncoder.encodeUTF8(Objects.toString(value)));
			send.send("&");
		}) ), SendType.form);
	}
	public static String post(String url, Consumer<whenSend> consumer, SendType type) throws IOException{
		URL myURL = new URL(url);
		HttpURLConnection httpsConn = (HttpURLConnection) myURL
				.openConnection();
		httpsConn.setDoOutput(true);
		httpsConn.setDoInput(true);
		httpsConn.setRequestProperty("Content-Type",type.ContentType);
		OutputStream out = httpsConn.getOutputStream();
		consumer.accept(new whenSend(out));
		out.close();
		BufferedInputStream read = new BufferedInputStream(httpsConn.getInputStream());
		StringBuilder sb = IO_tools.fromInToOut(read,new StringBuilder());
		read.close();
		return sb.toString();
	}
	public static String upload(String url,File file) throws IOException{
		URL myURL = new URL(url);
		HttpsURLConnection httpsConn = (HttpsURLConnection) myURL
				.openConnection();
		httpsConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundarywOUce20fskE00IRz");
		httpsConn.setDoOutput(true); 
		httpsConn.setDoInput(true);
		OutputStream out = httpsConn.getOutputStream();
		out.write("------WebKitFormBoundarywOUce20fskE00IRz\r\n".getBytes());
		out.write(("Content-Disposition: form-data; name=\"buffer\"; filename=\""+file.getName()+"\"\r\n").getBytes());
		out.write("Content-Type: image/jpeg\r\n".getBytes());
		out.write("\r\n".getBytes());
		
		FileInputStream in = new FileInputStream(file);
		IO_tools.fromInToOut(in, out);
		out.write("\r\n------WebKitFormBoundarywOUce20fskE00IRz--\r\n".getBytes());
		in.close();out.close();
		BufferedInputStream read = new BufferedInputStream(httpsConn.getInputStream());
		StringBuilder sb = IO_tools.fromInToOut(read,new StringBuilder());
		read.close();
		return sb.toString();
	}
	
}
