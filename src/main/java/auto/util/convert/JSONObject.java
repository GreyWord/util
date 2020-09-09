package auto.util.convert;

import auto.util.reflect.Normal_tools;
import auto.util.reflect.Reflect_tools;

import java.io.CharArrayReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.Map.Entry;

public class JSONObject{
	HashSet<Object> set;
	Object o;
	public JSONObject() { super(); }
	public JSONObject(Object o) {
		super(); 
		this.o=o;
		set = new HashSet<>();
	}
	private String warp(Object obj){
		String string = obj.toString();
		int strlen = string.length();
		CharArrayReader reader = new CharArrayReader(string.toCharArray());
		StringBuilder sb = new StringBuilder((strlen+strlen/10));
		sb.append("\"");
		try {
			int i = 0;
			while((i = reader.read())!=-1){
				if(i=='\\'||i=='\"'){
					sb.append("\\");
					sb.append((char)i);
				}else if(i=='\r'){
					sb.append("\\r");
				}else if(i=='\n'){
					sb.append("\\n");
				}else if(i=='\t'){
					sb.append("\\t");
				}else {
					sb.append((char)i);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		sb.append("\"");
		return sb.toString();
	}
	private String fullwarp(Object obj){
		StringBuilder sb = new StringBuilder("");
		if(obj==null){
			;
		} else if(obj instanceof String){
			return warp((obj));
		} else if(obj instanceof Number) {
			return obj.toString();
		} else if(obj instanceof Boolean) {
			return obj.toString();
		} else if(obj instanceof Date){
			return warp(Normal_tools.dateParse((Date) obj));
		} else if(obj instanceof Enum){
			return warp(obj);
		}else if (obj instanceof Collection) {
			Collection<?> list = (Collection<?>) obj;
			return fullwarp(list.toArray());
		} else if (obj instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) obj;
			sb.append("{ ");
			for (Entry<?, ?> entry : map.entrySet()) {
				sb.append(warp(entry.getKey()));
				sb.append(":");
				sb.append(fullwarp(entry.getValue()));
				sb.append(",");
			}
			sb.setCharAt(sb.length() - 1, '}');
			return sb.toString();
		} else if (obj.getClass().isArray()) {
			int i = Array.getLength(obj);
			sb.append("[ ");
			for(int j=0;j<i;j++){
				sb.append(fullwarp(Array.get(obj, j)));
				sb.append(',');
			}
			sb.setCharAt(sb.length() - 1, ']');
			return sb.toString();
		} else{
			if(set.add(obj)) 
				return fullwarp(Reflect_tools.getValueMap(obj));
		}
		return warp("");
	}
	@Override
	public String toString() {
		return fullwarp(o);
	}
	public static String toString(Object o) {
		return new JSONObject(o).toString();
	}
}
