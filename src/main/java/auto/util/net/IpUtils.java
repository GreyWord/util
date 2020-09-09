package auto.util.net;

import auto.util.convert.JSONString;
import auto.util.String_tools;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;
import java.util.function.Supplier;

/**
 * ip工具类
 * @Modified zhangxu
 * @since 1.8
 */
public class IpUtils {

	public static String getIpAddr(HttpServletRequest request) {
		/*String ip = request.getHeader("X-Forwarded-For");
		if (isunknown(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (isunknown(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (isunknown(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (isunknown(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (isunknown(ip)) {
			ip = request.getRemoteAddr();
		}*/
		String ip = getip(
				()->request.getHeader("X-Forwarded-For"),
				()->request.getHeader("Proxy-Client-IP"),
				()->request.getHeader("WL-Proxy-Client-IP"),
				()->request.getHeader("HTTP_CLIENT_IP"),
				()->request.getHeader("HTTP_X_FORWARDED_FOR"),
				request::getRemoteAddr
		);

		if (ip != null && ip.length() != 0 && ip.contains(",")) {
            String[] arr = ip.split(",");
			ip = Arrays.stream(arr).filter(IpUtils::notunknown).findFirst().get();
			/*if (arr != null && arr.length > 0) {
                for (String s : arr) {
                    if (unknownCheck(ip)) {
                        ip = s;
                        break;
                    }
                }
            }*/
        }
		
		return ip;
	}
	public static Long ip2Long(String ip){
		long result = 0;
		String[] ipAddressInArray = ip.split("\\.");
		if(ipAddressInArray.length>3)
		for (int i = 3; i >= 0; i--) {
			result |= Long.parseLong(ipAddressInArray[3 - i]) << (i * 8);
		}
		return result;
	}
	public static String long2Ip(long ip) {
		StringBuilder result = new StringBuilder(16);
		for (int i = 0; i < 4; i++) {
			result.insert(0,'.');
			result.insert(0,Long.toString(ip & 0xff));
			ip = ip >> 8;
		}
		result.deleteCharAt(result.length()-1);
		return result.toString();
	}
    public static String ip2Address(String ip) {
        try {
            String s = Net_tools.get("http://ip.taobao.com/service/getIpInfo.php?ip="+ip);
            if("".equals(s))return "";
            Map<String, String> data = new JSONString(s).getMap("data");
            return data.get("country")+" "+data.get("area")+" "+data.get("region")+" "+data.get("city")+" "+data.get("isp");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
	public static List<String> findiplist(){
		List<String> list = new ArrayList<>();
		findiplist(list);
		return list;
	}
	public static void findiplist(List<String> iplist){
		Enumeration allNetInterfaces = null;
		try {
			allNetInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		InetAddress ip;
		if(allNetInterfaces!=null)
			while (allNetInterfaces.hasMoreElements())
			{
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				Enumeration addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements())
				{
					ip = (InetAddress) addresses.nextElement();
					if (ip != null && ip instanceof Inet4Address)
						iplist.add(ip.getHostAddress());
				}
			}
	}
	private static boolean isunknown(String ip){
		return (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip));
	}
	private static boolean notunknown(String ip){
		return (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip));
	}
	private static String getip(Supplier<String>... actions){
		String ip=null;
		for(Supplier<String> supplier:actions){
			ip = supplier.get();
			if(notunknown(ip))break;;
		}
		return ip;
	}
}
