package auto.util.reflect;

public class Empty_tools {
    /**
     * 当任一对象为null时返回true
     * @param checks 待检查参数列表
     */
    public static boolean hasNULL(Object... checks){
        for(Object check:checks)
            if(check==null)
                return true;
        return false;
    }

    /**
     * 当所有对象都不为null时返回true
     * @param checks 待检查参数列表
     */
    public static boolean notNULL(Object... checks){
        for(Object check:checks)
            if(check==null)
                return false;
        return true;
    }

    /**
     * 当所有字符串都不为null且不为""时返回true
     * @param check 待检查参数
     */
    public static boolean isNULLString(Object check){
        if(check==null||check.equals(""))
            return true;
        return false;
    }

    /**
     * 当字符串都不为null且不为""时返回true
     * @param check 待检查参数
     */
    public static boolean notNULLString(Object check){
        if(check==null||check.equals(""))
            return false;
        return true;
    }
}
