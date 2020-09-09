package auto.util.reflect;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Normal_tools {
	private static SimpleDateFormat _formatdate = new SimpleDateFormat("yyyy-mm-dd");
	private static SimpleDateFormat _formatdatetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 格式化字符串，基于objclass的字段类型<br>
	 * @param objclass 类信息
	 * @param fieldName 字段名
	 * @param fieldValue 字段值
	 */
	public static Object parseObject(Class<?> objclass,String fieldName,String fieldValue){
		Class<?> type = Reflect_tools.getFieldTypeByName(objclass, fieldName);
		if(type==String.class)
			return fieldValue;
		if(type==Integer.class)
			return Integer.valueOf(fieldValue);
		if (type==Double.class)
			return Double.valueOf(fieldValue);
		if (type==Date.class)
			return parseSqlDate(fieldValue);
		if (type==Timestamp.class)
			return parseTimestamp(fieldValue);
		else
			return null;
	}
	/**
	 * 功能：正则匹配
	 * @param regex 匹配规则
	 * @param input 匹配内容
	 * @return 第一捕获组
	 */
	public static String matches(String regex, CharSequence input){
		Pattern p = Pattern.compile(regex);
		Matcher matcher = p.matcher(input);
		if(matcher.find())
			return matcher.group(1);
		return "";
	}
	/**
	 * 格式化字符串，基于objclass的字段类型<br>
	 * @param objclass 类信息
	 */
	public static Map<String, Object> parseMap(Class<?> objclass,Map<String, String[]> m){
		HashMap<String, Object> hm = new HashMap<String, Object>();
		Object result = null;
		for (String string : m.keySet()) {
			String[] strings = m.get(string);
			if(strings.length==1)
				result = parseObject(objclass,string, m.get(string)[0]);
			else if(strings.length==0)
				continue;
			else
				result = parseObject(objclass,string, Array_tools.ArraytoString(strings,","));
			if(result==null)
				continue;
			hm.put(string, result);
		}
		return hm;
	}
	/**
	 * 格式化数字，变长参数，依次格式化<br>
	 * 取第一个成功的值，默认值是0<br>
	 * @param nums
	 */
	public static int parseInt(String... nums){
		int defaultnum = 0;
		for(String num:nums)
			if(Empty_tools.notNULLString(num)){
				try{
					defaultnum = Integer.parseInt(num);
					break;
				}catch(NumberFormatException e){
					e.printStackTrace();
				}
			}
		return defaultnum;
	}
	/**
	 * 将字符串格式化为java.util.Date
	 * @param value 要求与SimpleDateFormat格式相符
	 * @param formatdate 为null时以yyyy-mm-dd进行格式化
	 * @return {@link java.util.Date}
	 */
	public static java.util.Date parseDate(String value, SimpleDateFormat formatdate){
		java.util.Date date = null;
		try {
			if(formatdate==null|| Empty_tools.isNULLString(formatdate.toPattern()))
				date = _formatdate.parse(value);
			else
				date = formatdate.parse(value);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	public static String dateParse(java.util.Date date){
		if(date instanceof Timestamp)
			return _formatdatetime.format(date);
		else
			return _formatdate.format(date);
	}
	/**
	 * 将字符串格式化为java.sql.Date
	 * @param value "yyyy-mm-dd"格式
	 * @return {@link Date}
	 */
	public static Date parseSqlDate(String value){
		return new Date(parseDate(value,_formatdate).getTime());
	}
	/**
	 * 将字符串格式化为java.sql.Timestamp
	 * @param value "yyyy-MM-dd HH:mm:ss"格式
	 * @return {@link Timestamp}
	 */
	public static Timestamp parseTimestamp(String value){
		return new Timestamp(parseDate(value,_formatdatetime).getTime());
	}

}
