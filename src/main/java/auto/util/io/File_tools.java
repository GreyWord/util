package auto.util.io;

import java.io.*;
import java.nio.charset.Charset;

public class File_tools {
    public static void write(String path,String inner,String charset){
        write(new File(path),inner,charset);
    }
    public static void write(File file,String inner,String charset){
        try {
            if(!file.exists()) file.createNewFile();
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), Charset.forName(charset));
            writer.write(inner);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
