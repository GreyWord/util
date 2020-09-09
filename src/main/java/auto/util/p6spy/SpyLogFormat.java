package auto.util.p6spy;

import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.spy.appender.SingleLineFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

import static java.text.MessageFormat.format;

/**
 * Created by ZhangXu on 2017/9/1.
 */
public class SpyLogFormat extends SingleLineFormat {
    private Logger log = LoggerFactory.getLogger("p6spy");
    static Pattern lineBreakPattern = Pattern.compile("(\\r?\\n\\s+)+");
    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql) {
        String result = format("{0}|connection {1}|{2}|cost {3}|{4}|{5}",now,connectionId,category,elapsed,singleLine(prepared),singleLine(sql));
        return result;
    }
    public static String singleLine(String str) {
        return lineBreakPattern.matcher(str).replaceAll(" ");
    }
}
