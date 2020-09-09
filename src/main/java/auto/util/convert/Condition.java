package auto.util.convert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.*;

/**
 * @author Created by ZhangXu on 2018/5/16.
 */
public class Condition {

    /**不是method的字符*/
    private static final List<Character> list =
            Arrays.asList('[', '(', '{',']', ')', '}', '\'', '"',
                        '0','1','2','3','4','5','6','7','8','9');
    /**运算符*/
    private static final List<String> list1 = Arrays.asList("+", "-", "*", "/");
    /**比较符*/
    private static final List<String> list2 =
            Arrays.asList("<", ">", "<=", ">=", "=", "==", "!=","in","包含","不包含","从属");
    /**逻辑符*/
    private static final List<String> list3 = Arrays.asList("&&", "||","and", "or");

    private static Logger log = LoggerFactory.getLogger(Condition.class);

    private PushbackReader json;
    private Object obj = null;
    private Map<String,Object> map;
    private String method;
    private int i = 0;
    public Condition(Map<String,Object> map,String json) {
        this.map = map;

        this.json = new PushbackReader(new CharArrayReader(json.toCharArray()));
    }
    public Object toObject(){
        if(obj!=null) return obj;
        try {
            obj =  readObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return obj;
    }
    private Object readObject() throws IOException{
        Object value = null ;
        try {
            while((i = json.read())!=-1){
                if(i=='\"' || i=='\'')
                    value = getString(i);
                else if(Character.isDigit(i)||i=='-'){
                    value = getNumber();
                    //getNumber会多读一位，需要强制break
                    break;
                }
                else if(i=='{')//获取引用
                   value = getRef();
                else if(i=='[')//获取数组
                    value = getArray();
                else if(i=='(') {//获取表达式
                    value = getEval(); //表达式从(开始,从)结束
                    if(value!=null)//表达式无值不多读
                        i = json.read();
                }else if(i=='!')//表达式取非
                    value = not(getSimEval());
                else if(i==',')//数组下一位
                    break;
                else if(i=='，')//数组下一位（中文逗号兼容）
                    break;
                else if(i=='}')//引用结束
                    break;
                else if(i==']')//数组结束
                    break;
                else if(i==')')//表达式结束
                    break;
                if(value!=null){//有值
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }
    private Object getRef() throws IOException{
        String key = getString('}');
        return map.get(key);
    }
    private Object getEval() throws IOException{
        Object left = readObject();
        while ((method = getMethod()).length()>0){
            if(list3.contains(method)){
                //是逻辑判断符,需要获取完整表达式
                //但此时method未清空,会干扰获取过程
                String m = method;
                method=null;
                Object right = getSimEval();
                switch (m){
                    default:
                    case "&&":
                    case "and":
                        left = is(left) && is(right);
                        break;
                    case "||":
                    case "or":
                        left = is(left) || is(right);
                }
            }else if(list2.contains(method)){
                //是普通比较符
                Object right = readObject();
                left=list2execute(method,left,right);
                method=null;
            }else if(list1.contains(method)){
                //是运算符
                Object right = readObject();
                left=list1execute(method,left,right);
                method=null;
            }else{//不识别的方法
                method=null;
            }
        }
        return left;
    }
    private Object getSimEval() throws IOException{
        Object left = readObject();
        while ((method = getMethod()).length()>0){
            if(list3.contains(method)){
                //是逻辑判断符
                break;
            }else if(list1.contains(method)){
                //是运算符
                Object right = readObject();
                left=list1execute(method,left,right);
            }else if(list2.contains(method)){
                //是普通比较符
                Object right = readObject();
                left=list2execute(method,left,right);
            }
            method=null;
        }
        return left;
    }
    private static boolean not(Object o){
        return !is(o);
    }
    private static boolean is(Object o){
        if(o==null)return false;
        if(o instanceof Boolean)
            return (boolean)o;
        else if(o instanceof String)
            return !"".equals(o);
        return true;
    }
    /**运算符,空值计为0*/
    private Object list1execute(String method,Object le,Object ri){
        if(le==null||ri==null)return null;
        double left,right;
        if("+".equals(method)){
            //如果数字相加,并且ri为空
            if(le instanceof Number){
                if("".equals(ri))
                    ri="0";
            }else
                return le.toString()+ri.toString();
        }
        left = doubleLong(le).doubleValue();
        right = doubleLong(ri).doubleValue();
        switch (method){
            case"+":
                return left+right;
            case"-":
                return left-right;
            case"*":
                return left*right;
            case"/":
                if(right!=0)
                    return left/right;
                else
                    return 0;
        }
        return "";
    }
    /**比较符运算,默认返回true*/
    private boolean list2execute(String method,Object le,Object ri){
        if(le==null||ri==null)return true;
        double left,right;
        if(le instanceof Number || ri instanceof Number){
            //数字的比较大小
            left = doubleLong(le).doubleValue();
            right = doubleLong(ri).doubleValue();
            return list2execute(method,left,right);
        }else if(method.equals("in")) {
            //字符的包含关系
            return !(ri instanceof Collection) || ((Collection) ri).contains(le);
        }else if(method.equals("包含")) {
            if(ri instanceof Collection){
                //字符包含数组中的任意一个
                String s = le.toString();
                for(Object o : (Collection) ri){
                    if(s.contains(o.toString()))
                        return true;
                }
                return false;
            }else if(le instanceof Collection) {
                //数组包含字符
                return ((Collection) le).contains(ri.toString());
            }
            //字符包含字符
            return le.toString().contains(ri.toString());
        }else if(method.equals("不包含")) {
            return not(list2execute("包含",le,ri));
        }else if(method.equals("从属")) {
            String s = le.toString();
            if(ri instanceof Collection){
                //字符包含数组中的任意一个
                for(Object o : (Collection) ri){
                    if(s.contains("\"" + o + '"'))
                        return true;
                }
                return false;
            }else {
                //字符包含字符
                return s.contains("\"" + ri + '"');
            }
        }
        else {
            //字符的比较大小
            return list2execute(method,le.toString(),ri.toString());
        }

    }
    private boolean list2execute(String method,double left,double right){
        switch (method){
            case"<":
                return left<right;
            case">":
                return left>right;
            case"<=":
                return left<=right;
            case">=":
                return left>=right;
            case"=":
            case"==":
                return left==right;
            case"!=":
                return left!=right;
        }
        return true;
    }
    private boolean list2execute(String method,String left,String right){
        switch (method){
            case"<":
                return left.compareTo(right)<0;
            case">":
                return left.compareTo(right)>0;
            case"<=":
                return left.compareTo(right)<=0;
            case">=":
                return left.compareTo(right)>=0;
            case"=":
            case"==":
                return left.compareTo(right)==0;
            case"!=":
                return left.compareTo(right)!=0;
            case"从属":{
                //从属关系，前者是toJson的数组，后者是单独字段

                return left.contains('"' + right + '"');
            }
        }
        return true;
    }
    private List<Object> getArray() throws IOException{
        ArrayList<Object> list = new ArrayList<Object>();
        Object value = null;
        boolean canAdd = true;
        while(true){
            value = readObject();
            if(value !=null&&canAdd){
                list.add(value);
                canAdd = false;
            }
            if(i==']')
                break;
            else if(i==',' || i=='，')
                canAdd=true;
            else if(i==-1)
                break;
        }
        return list;
    }
    private String getString(int split) throws IOException{
        StringBuilder sb = new StringBuilder();
        while((i = json.read())!=-1){
            if(i=='\\'){
                i = json.read();
            }else if(i==split) break;
            sb.append((char)i);
        }
        return sb.toString();
    }
    private String getMethod() throws IOException{
        if(method!=null&&method.length()>0)return method;
        boolean start = true;
        StringBuilder sb = new StringBuilder();
        while((i = json.read())!=-1){
            if(list.contains((char)i)){
                json.unread(i);
                break;
            }
            if(start){
                if(i!=' '){
                    start=false;
                    sb.append((char)i);
                }
            }else {
                if(i!=' ')
                    sb.append((char)i);
                else
                    break;
            }
        }
        return sb.toString();
    }
    private Number getNumber() throws IOException{
        StringBuilder sb = new StringBuilder();
        sb.append((char)i);
        while((i = json.read())!=-1){
            if(Character.isDigit(i)||i=='.')
                sb.append((char)i);
            else{
                json.unread(i);
                break;
            }
        }
        return doubleLong(sb.toString());
    }
    private Number doubleLong(Object source){
        if(source instanceof Number){
            return (Number)source;
        }
        String result=source.toString();
        if(result.matches("(-)?\\d+\\.\\d+"))//*.*
            return Double.parseDouble(result);
        else if(result.matches("(-)?\\d+"))
            return Long.parseLong(result);
        else
            return 0;
    }
    static boolean eval(Map<String,Object> map,String condition){
        if(condition==null||condition.length()==0)return true;
        log.info("条件：{},参数{}",condition,map);
        try {
            Condition condition1 = new Condition(map, condition);
            final Object eval = condition1.getEval();
            condition1.json.close();
            if(eval==null)return true;
            return is(eval);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
    //( {内部调岗} == "flase"  && {薪酬无变化} == "flase"  && {薪酬变化} == "true"  && {跨公司调岗} == "true" )  && {发起者所属部门}  从属 ['燕星宇市场','国锦利民'],
    //参数{到岗时间=, 现岗位名称=, 内部调岗=true, 薪酬无变化=true, 调入部门=集团投资部, 备注=, 调岗人姓名=张旭, 调入公司=, 跨公司调岗=false, 薪酬变化=false, 发起者岗位=职员, 发起者所属部门=["集团管理部门","集团信息服务部"], 调后岗位名称=, 现所在部门=集团财务部, attach=[], 现所在公司=, 发起者部门=集团信息服务部}

    public static void main(String[] args) {
        Map<String,Object> map = new HashMap<>();
        map.put("发起者所属部门","[\"集团管理部门\",\"综合服务事业部\",\"燕星宇市场\",\"燕星宇招商部\"]");
        map.put("内部调岗","true");
        map.put("薪酬无变化","true");
        map.put("薪酬变化","flase");
        map.put("跨公司调岗","flase");
        map.put("d","-7000");
        //String condition = "[部门(所属部门)] == \"综管部(包含子部门),燕星宇市场(包含子部门),国锦利民(包含子部门),田园利民(包含子部门)\"";
        //String condition = "(['b','c']包含't')and('a'=='a')";
        String condition = "( {内部调岗} == \"true\"  && {薪酬无变化} == \"true\"  && {薪酬变化} == \"flase\"  && {跨公司调岗} == \"flase\" )  && {发起者所属部门}  从属 ['集团管理部门','油品营销中心','车队','油库','质检科','油品事业总经办','建筑开发事业部']";
        final boolean eval = Condition.eval(map, condition);
        System.out.println(eval);

    }
}