package auto.util.convert;

import java.io.CharArrayReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONString {
	CharArrayReader json;
	private Object obj = null;
	private Map<String, Object> map = null;
	private List<Object> array = null;
	int i = 0;
	public JSONString(){}
	public JSONString(String json) {
		this.json = new CharArrayReader(json.toCharArray());
	}
	public Object toObject(){
		if(obj!=null) return obj;
		try {
			Object readObject = readObject();
			obj = readObject;
			if(readObject instanceof Map){
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) readObject;
				this.map = map;
			}else if(readObject instanceof List){
				this.array = (List<Object>)readObject;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return obj;
	}
	public Map<String,Object> toMap(){
		toObject();
		return map;
	}
	public <T> Map<String,T> getMap(String key){
		toObject();
        final Map<String,T> o = (Map<String,T>)map.get(key);
        return o;
	}
	public List<Object> toArray(){
		toObject();
		return array;
	}
	private Object readObject() throws IOException{
		Object value = null ;
		try {
			while((i = json.read())!=-1){
				if(i=='\"' || i=='\'')
					value = getString();
				else if(i=='t' && json.read()=='r' && json.read()=='u' && json.read()=='e')
					value=true;
				else if(i=='f' && json.read()=='a' && json.read()=='l' && json.read()=='s' && json.read()=='e')
					value=false;
				else if(i=='n' && json.read()=='u' && json.read()=='l' && json.read()=='l')
					value=null;
				else if(Character.isDigit(i)){
					value = getNumber();
					//getNumber会多读一位，需要强制break
					break;
				}else if(i=='-'){//负数的Number
					i = json.read();
					value = "-"+getNumber();
					//getNumber会多读一位，需要强制break
					break;
				}else if(i=='{')
					value = getMap();
				else if(i=='[')
					value = getArray();
				else if(i==':')
					break;
				else if(i==',')
					break;
				else if(i=='}')
					break;
				else if(i==']')
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;
	}
	private Map<String,Object> getMap() throws IOException{
		Map<String,Object> map = new HashMap<String, Object>();
		String name = null;
		Object value = null;
		boolean isName = true;
		while(true){
			value = readObject();
			if(value !=null){
				if(isName)
					name = value.toString();
				else
					map.put(name, value);
				value = null;
			}
			
			if(i=='}')
				break;
			else if(i==':')
				isName = false;
			else if(i==','){
				isName = true;
				name=null;
			}
		}
		return map;
	}
	private List<Object> getArray() throws IOException{
		ArrayList<Object> list = new ArrayList<Object>();
		Object value;
		boolean canAdd = true;
		while(true){
			value = readObject();
			if(value !=null&&canAdd){
				list.add(value);
				canAdd = false;
			}
			if(i==']')
				break;
			else if(i==',')
				canAdd=true;
			else if(i==-1)
				break;
		}
		return list;
	}
	private String getString() throws IOException{
		int split = i;
		StringBuilder sb = new StringBuilder();
		while((i = json.read())!=-1){
			if(i=='\\'){
				i = json.read();
				if(i=='u'){
					int cp = Integer.parseInt(read(4), 16);
					sb.appendCodePoint(cp);
				}else if(i=='r'){
					sb.append("\r");
				}else if(i=='n'){
					sb.append("\n");
				}else if(i=='t'){
					sb.append("\t");
				}else {
					sb.append((char)i);
				}
				continue;
			}else if(i==split) break;
			sb.append((char)i);
		}
		return sb.toString();
	}
	private String getNumber() throws IOException{
		StringBuilder sb = new StringBuilder();
		sb.append((char)i);
		while((i = json.read())!=-1){
			if(Character.isDigit(i)||i=='.')
				sb.append((char)i);
			else break;
		}
		return sb.toString();
	}
	private String read(int i) throws IOException {
		char[] chars = new char[i];
		int j=i-1;
		while (i-->0){
			chars[j-i]= (char)json.read();
		}
		return new String(chars);
	}
	public static Map<String,Object> toMap(String json){
		return new JSONString(json).toMap();
	}
	public static Map<String,Object> toMap(String json,String... append){
        final Map<String, Object> map = new JSONString(json).toMap();
        if(append!=null) for(String ap:append) map.putAll(toMap(ap));
        return map;
    }
	public static List<Object> toArray(String json){
		return new JSONString(json).toArray();
	}
	@Override
	public String toString() {
		return this.toObject().toString();
	}
}
