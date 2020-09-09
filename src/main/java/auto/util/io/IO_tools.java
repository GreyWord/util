package auto.util.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by ZhangXu on 2017/7/31.
 */
public class IO_tools {
	public static void fromInToOut(InputStream in,OutputStream out) throws IOException{
		byte[] b = new byte[1024];
		int i;
		while((i=in.read(b))!=-1)
			out.write(b, 0, i);
	}
	public static void fromInToOutAutoClose(InputStream in,OutputStream out) throws IOException{
		fromInToOut(in, out);
		in.close();
		out.close();
	}
	public static <T extends Appendable> T fromInToOut(InputStream in,T out) throws IOException{
		InputStreamReader reader = new InputStreamReader(in, "utf8");
		int i = 0;
		while((i=reader.read())!=-1)
			out.append((char)i);
		return out;
	}
}
