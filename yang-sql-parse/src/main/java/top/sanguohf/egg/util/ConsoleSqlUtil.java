package top.sanguohf.egg.util;

import java.math.BigDecimal;
import java.util.List;

public class ConsoleSqlUtil {

    /**
     * 打印SQL语句
     * */
    public static void console(String sql){
        System.out.println("执行的SQL语句："+sql);
    }

    public static void consoleParam(List<Object> params){
        System.out.print("参数是：");
        StringBuilder builder = new StringBuilder();
        int i =0;
        int size = params.size();
        for(Object os : params){
            if(os instanceof String|os instanceof Integer|os instanceof Long|os instanceof Byte|os instanceof Double|os instanceof Character|os instanceof CharSequence
                    |os instanceof Short|os instanceof Boolean|os instanceof Integer){
                builder.append(os).append(" ");
            }else if(os instanceof BigDecimal){
                builder.append(((BigDecimal) os).toPlainString()).append(" ");
            }
            if(i!=size-1)
                builder.append(",");
            i++;
        }
        System.out.println(builder.toString());
    }
    public static void consoleParams(List<Object[]> params){
        System.out.print("参数是：");

        for(Object[] pk : params) {
            int i =0;
            int size = pk.length;
            StringBuilder builder = new StringBuilder();
            for (Object os : pk) {
                if (os instanceof String | os instanceof Integer | os instanceof Long | os instanceof Byte | os instanceof Double | os instanceof Character | os instanceof CharSequence
                        | os instanceof Short | os instanceof Boolean | os instanceof Integer) {
                    builder.append(os).append(" ");
                } else if (os instanceof BigDecimal) {
                    builder.append(((BigDecimal) os).toPlainString()).append(" ");
                }
                if(i!=size-1)
                    builder.append(",");
                i++;
            }
            System.out.println(builder.toString());
        }
    }

    public static void consoleResult(Object os){
        System.out.print("结果是：");
        if(os instanceof List){
            for(Object one : (List)os){
                System.out.println(one);
            }
        }else System.out.println(os);
    }
}
