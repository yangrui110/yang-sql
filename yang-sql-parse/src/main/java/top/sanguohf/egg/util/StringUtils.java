package top.sanguohf.egg.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static top.sanguohf.egg.reflect.ReflectEntity.getTableField;

public class StringUtils {

    public static boolean isEmpty(String string){
        if(string==null||"".equals(string)){
            return true;
        }
        return false;
    }

    /**
     * 下划线转驼峰法(默认小驼峰)
     *
     * @param line
     *            源字符串
     * @param smallCamel
     *            大小驼峰,是否为小驼峰(驼峰，第一个字符是大写还是小写)
     * @return 转换后的字符串
     */
    public static String underline2Camel(String line, boolean ... smallCamel) {
        if (line == null || "".equals(line)) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        Pattern pattern = Pattern.compile("([A-Za-z\\d]+)(_)?");
        Matcher matcher = pattern.matcher(line);
        //匹配正则表达式
        while (matcher.find()) {
            String word = matcher.group();
            //当是true 或则是空的情况
            if((smallCamel.length ==0 || smallCamel[0] ) && matcher.start()==0){
                sb.append(Character.toLowerCase(word.charAt(0)));
            }else{
                sb.append(Character.toUpperCase(word.charAt(0)));
            }

            int index = word.lastIndexOf('_');
            if (index > 0) {
                sb.append(word.substring(1, index).toLowerCase());
            } else {
                sb.append(word.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    /**
     * 驼峰法转下划线
     *
     * @param line
     *            源字符串
     * @return 转换后的字符串
     */
    public static String camel2Underline(String line) {
        if (line == null || "".equals(line)) {
            return "";
        }
        line = String.valueOf(line.charAt(0)).toUpperCase()
                .concat(line.substring(1));
        StringBuffer sb = new StringBuffer();
        Pattern pattern = Pattern.compile("[A-Z]([a-z\\d]+)?");
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            String word = matcher.group();
            sb.append(word.toUpperCase());
            sb.append(matcher.end() == line.length() ? "" : "_");
        }
        return sb.toString();
    }
    /**
     * 替换condition
     * */
    public static String patternReplace(Map<String,Class> map,String tableAlias,String condition) throws NoSuchFieldException, ClassNotFoundException {
        String patter = "(?<=%s\\.).*?(?==| |>|<|$)";
        String format = String.format(patter, tableAlias);
        Pattern compile = Pattern.compile(format);
        Matcher matcher = compile.matcher(condition);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()){
            String group = matcher.group();
            String tableField = getTableField(map.get(tableAlias), group);
            matcher.appendReplacement(buffer,tableField);
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

}
