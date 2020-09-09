import auto.util.convert.JSONString;
import auto.util.convert.XMLToJson;
import auto.util.date.Date_tools;
import auto.util.date.TimeCompare;
import auto.util.date.TimeFormat;
import auto.util.date.Timmer;
import auto.util.net.FtpSender;
import auto.util.net.Net_tools;
import org.apache.commons.net.ftp.FTPClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ZhangXu on 2017/10/9.
 */
public class Test {
    public static void main(String[] args) throws IOException {
        //53CAB*46#DAf
        //I4,.86aa+2d*
        //System.out.println(RandomUtil.ramdom(12));
        //ftp();
        //Calendar calendar = Date_tools.dayTime(null, null, null, 1);
        ;
        //System.out.println(TimeFormat.format(TimeFormat.FormatType.date,Date_tools.rollDay(calendar,-1).getTime().getTime()));
        System.out.println(XMLToJson.xml2map("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "\n" +
                "<Device_Model>\n" +
                " <Item device_id=\"7c6d5b3e-b42c-4c3c-8ab6-f14bcec2207e\" device_name=\"柜1-备用1\" bay_id=\"b01dcf6f-14a2-4c12-8e2f-de4c3976774a\" bay_name=\"休息室\" main_device_id=\"0847bdac-5d3f-4278-9f23-c87a66dc7cce\" main_device_name=\"设备\" device_type=\"5\" meter_type=\"19\" appearance_type=\"null\" save_type_list=\"3\" recognition_type_list=\"1\" phase=\"\" device_info=\"null\"/>\n" +
                " <Item device_id=\"39988687-e6c2-4338-b5a2-a288578d8e8e\" device_name=\"柜1-备用2\" bay_id=\"b01dcf6f-14a2-4c12-8e2f-de4c3976774a\" bay_name=\"休息室\" main_device_id=\"0847bdac-5d3f-4278-9f23-c87a66dc7cce\" main_device_name=\"设备\" device_type=\"5\" meter_type=\"19\" appearance_type=\"null\" save_type_list=\"3\" recognition_type_list=\"1\" phase=\"\" device_info=\"null\"/>\n" +
                " <Item device_id=\"e5098d46-b41f-465d-aca4-c36ba476023f\" device_name=\"柜1-备用3\" bay_id=\"b01dcf6f-14a2-4c12-8e2f-de4c3976774a\" bay_name=\"休息室\" main_device_id=\"0847bdac-5d3f-4278-9f23-c87a66dc7cce\" main_device_name=\"设备\" device_type=\"5\" meter_type=\"19\" appearance_type=\"null\" save_type_list=\"3\" recognition_type_list=\"1\" phase=\"\" device_info=\"null\"/>\n" +
                " <Item device_id=\"1b4e67b4-c6d4-4066-a5c6-106eeedc5902\" device_name=\"保护柜1-检修状态投入\" bay_id=\"b01dcf6f-14a2-4c12-8e2f-de4c3976774a\" bay_name=\"休息室\" main_device_id=\"0847bdac-5d3f-4278-9f23-c87a66dc7cce\" main_device_name=\"设备\" device_type=\"5\" meter_type=\"19\" appearance_type=\"null\" save_type_list=\"3\" recognition_type_list=\"1\" phase=\"\" device_info=\"null\"/>\n" +
                " <Item device_id=\"5a3de36e-4b5a-4e7e-abd1-9b13edf84c44\" device_name=\"保护柜1-远方操作投入\" bay_id=\"b01dcf6f-14a2-4c12-8e2f-de4c3976774a\" bay_name=\"休息室\" main_device_id=\"0847bdac-5d3f-4278-9f23-c87a66dc7cce\" main_device_name=\"设备\" device_type=\"5\" meter_type=\"19\" appearance_type=\"null\" save_type_list=\"3\" recognition_type_list=\"1\" phase=\"\" device_info=\"null\"/>\n" +
                " <Item device_id=\"6fa95289-7931-49db-9a8a-70be24ce8489\" device_name=\"线路保护柜-备用1\" bay_id=\"b01dcf6f-14a2-4c12-8e2f-de4c3976774a\" bay_name=\"休息室\" main_device_id=\"0847bdac-5d3f-4278-9f23-c87a66dc7cce\" main_device_name=\"设备\" device_type=\"5\" meter_type=\"19\" appearance_type=\"null\" save_type_list=\"3\" recognition_type_list=\"1\" phase=\"\" device_info=\"null\"/>\n" +
                " <Item device_id=\"4cfd5f53-9804-4dfe-8149-1f3c27d66edb\" device_name=\"线路保护柜-备用2\" bay_id=\"b01dcf6f-14a2-4c12-8e2f-de4c3976774a\" bay_name=\"休息室\" main_device_id=\"0847bdac-5d3f-4278-9f23-c87a66dc7cce\" main_device_name=\"设备\" device_type=\"5\" meter_type=\"19\" appearance_type=\"null\" save_type_list=\"3\" recognition_type_list=\"1\" phase=\"\" device_info=\"null\"/>\n" +
                " <Item device_id=\"453eab72-b553-4ed3-b884-e08e65eff5ed\" device_name=\"线路保护柜-1出口\" bay_id=\"b01dcf6f-14a2-4c12-8e2f-de4c3976774a\" bay_name=\"休息室\" main_device_id=\"0847bdac-5d3f-4278-9f23-c87a66dc7cce\" main_device_name=\"设备\" device_type=\"5\" meter_type=\"19\" appearance_type=\"null\" save_type_list=\"3\" recognition_type_list=\"1\" phase=\"\" device_info=\"null\"/>\n" +
                " <Item device_id=\"8dcba6ec-85cc-4dc7-9a80-5fe03df56e75\" device_name=\"线路保护柜-2出口\" bay_id=\"b01dcf6f-14a2-4c12-8e2f-de4c3976774a\" bay_name=\"休息室\" main_device_id=\"0847bdac-5d3f-4278-9f23-c87a66dc7cce\" main_device_name=\"设备\" device_type=\"5\" meter_type=\"19\" appearance_type=\"null\" save_type_list=\"3\" recognition_type_list=\"1\" phase=\"\" device_info=\"null\"/>\n" +
                " <Item device_id=\"dc5b2488-1a09-46a0-ad16-2b2074659a33\" device_name=\"母线柜-备用2-1\" bay_id=\"b01dcf6f-14a2-4c12-8e2f-de4c3976774a\" bay_name=\"休息室\" main_device_id=\"0847bdac-5d3f-4278-9f23-c87a66dc7cce\" main_device_name=\"设备\" device_type=\"5\" meter_type=\"19\" appearance_type=\"null\" save_type_list=\"3\" recognition_type_list=\"1\" phase=\"\" device_info=\"null\"/>\n" +
                " <Item device_id=\"014e811d-e6b9-4a1d-bd18-103af5626e4e\" device_name=\"母线柜-备用2-2\" bay_id=\"b01dcf6f-14a2-4c12-8e2f-de4c3976774a\" bay_name=\"休息室\" main_device_id=\"0847bdac-5d3f-4278-9f23-c87a66dc7cce\" main_device_name=\"设备\" device_type=\"5\" meter_type=\"19\" appearance_type=\"null\" save_type_list=\"3\" recognition_type_list=\"1\" phase=\"\" device_info=\"null\"/>\n" +
                " \n" +
                "</Device_Model>").get("Item"));
    }
    static void ftp() throws IOException {
        Timmer.start();
        FTPClient ftpClient = new FTPClient();
        ftpClient.setControlEncoding("utf8");
        ftpClient.connect("www.fstpc.net");
        ftpClient.login("nginx","nginx");
        ftpClient.changeWorkingDirectory("oa");
        //ftpClient.storeFile("asd.fs",new FileInputStream(new File("C:\\Users\\张旭\\Desktop/java工程师.docx")));
        ftpClient.logout();
        ftpClient.disconnect();
        System.out.println(Timmer.end());
    }
    static void ftp2() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();;
        FtpSender.setconf("172.16.99.11","nginx","nginx","utf8","http://www.fstpc.net/html","oa");
        FtpSender.getUrlFile("http://www.fstpc.net/html/oa/code/96181/96181c33-a430-4c96-ad78-e29ae2a45029.png",byteArrayOutputStream);
        System.out.println(byteArrayOutputStream.size());
    }
}
