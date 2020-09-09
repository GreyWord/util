package auto.util;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.Map;

/**
 * Created by ZhangXu on 2017/9/4.
 */
public class Freemaker {
    private static String path;
    private static String templapath;
    private static String staticpath;

    static Configuration configuration = new Configuration();
    public static void make(String source,Map<String,Object> paramMap,String target){
        try {
            //获取或创建一个模版。
            Template template = configuration.getTemplate(source);
            Writer writer  = new OutputStreamWriter(new FileOutputStream(getStatFile(target)),"UTF-8");
            template.process(paramMap, writer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }
    private static File getFile(String name){
        return new File(path,name);
    }
    private static File getTempFile(String name){
        return new File(templapath,name);
    }
    public static File getStatFile(String name){
        return new File(staticpath,name);
    }

    public static void setPath(String path) {
        Freemaker.path = path;
        templapath=path+"/templates";
        staticpath=path+"/static";
        //创建一个合适的Configration对象
        try {
            configuration.setDirectoryForTemplateLoading(getFile("/templates"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        configuration.setObjectWrapper(new DefaultObjectWrapper());
        configuration.setDefaultEncoding("UTF-8");
    }
}
