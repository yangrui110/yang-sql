package top.sanguohf.egg;

import com.alibaba.fastjson.JSONObject;
import top.sanguohf.egg.base.EntityOrderBy;
import top.sanguohf.egg.constant.DbType;
import top.sanguohf.egg.ops.EntityDeleteSql;
import top.sanguohf.egg.ops.EntityPageSql;
import top.sanguohf.egg.ops.EntitySelectSql;
import top.sanguohf.egg.param.EntityParamParse;
import top.sanguohf.egg.param.EntityParams;
import top.sanguohf.egg.reflect.UserClassesView;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainTest {

    public static void mainb(String[] args) throws ClassNotFoundException, NoSuchFieldException {
        EntityParams params=new EntityParams();
        JSONObject os=new JSONObject();
        os.put("left","userName");
        os.put("right","123456");
        os.put("relation","=");
        //
        JSONObject os2 = new JSONObject();
        os2.put("left","password");
        os2.put("right",888);
        os2.put("relation","=");
        List ls =new LinkedList<>();
        ls.add(os);
        ls.add(os2);
        JSONObject com1=new JSONObject();
        com1.put("condition",ls);
        com1.put("combine","or");
        /*JSONObject com=new JSONObject();
        com.put("condition",ls);
        com.put("combine","and");
        params.setCondition(com);*/

        JSONObject os3=new JSONObject();
        os3.put("left","userName");
        os3.put("right","67755");
        os3.put("relation","=");
        //
        JSONObject os4 = new JSONObject();
        os4.put("left","password");
        os4.put("right","abc");
        os4.put("relation","=");
        List ls2 =new LinkedList<>();
        ls2.add(os3);
        ls2.add(os4);
        JSONObject com2=new JSONObject();
        com2.put("condition",ls2);
        com2.put("combine","or");

        JSONObject l1 = new JSONObject();
        l1.put("left",com1);
        l1.put("right",com2);
        l1.put("relation","and");
        params.setCondition(l1);
        params.setTableClassName("top.sanguohf.egg.reflect.User");
        EntityOrderBy orderBy1=new EntityOrderBy();
        orderBy1.setColumn("userName");
        orderBy1.setDirect("desc");
        EntityOrderBy orderBy2 =new EntityOrderBy();
        List order = new LinkedList<>();
        order.add(orderBy1);
        //params.setOrderBy(order);
        JSONObject os0=new JSONObject();
        os0.put("userName","admin1");
        os0.put("password","11122");
        //params.setData(os0);
        EntitySelectSql selectSql=new EntityParamParse(params).parseToEntitySelectSql();
        System.out.println(selectSql.toSql());
        System.out.println(selectSql.sqlOne(true));
        LinkedList linkedList = new LinkedList<>();
        selectSql.addValue(linkedList);
        System.out.println(linkedList);
       // System.out.println(selectSql.);
        //System.out.println(new EntityPageSql(selectSql).toPageSql(1,10, DbType.ORACLE));
        //System.out.println(new EntityPageSql(selectSql).toCountSql(DbType.SQL));
        //ReflectEntity entity = new ReflectEntity();
        //System.out.println(entity.reflectSelectColumns(User.class));
    }

    public static void maint(String[] args) throws ClassNotFoundException, NoSuchFieldException {
        JSONObject os=new JSONObject();
        os.put("password","pmmm");
        os.put("userName","yang");
        os.put("id","99");
        EntityParams params=new EntityParams();
        params.setTableClassName("top.sanguohf.egg.reflect.User");
        params.setCondition(os);
       // new EntityParamParse(params).parseToEntityInertSql();
        //new EntityParamParse(params).parseToEntityUpdateSql();
        EntityDeleteSql deleteSql = new EntityParamParse(params).parseToEntityDeleteSql();
        //EntitySelectSql selectSql = new EntityParamParse(params).parseToEntitySelectSql();
        //System.out.println(selectSql.toSql());
    }

    public static void main(String[] args) throws ClassNotFoundException {
        String content = "useriiii.userName=1 and user.password = 2 and user.id=3 and user.ps>0 and user.id > [ones] and user.po < [pj]";
        String patter = "(?<=user\\.).*?(?==| |>|<)";
        Pattern compile = Pattern.compile(patter);
        Matcher matcher = compile.matcher(content);
        StringBuffer stringBuffer = new StringBuffer();
        while (matcher.find()){
            String group = matcher.group();
            matcher.appendReplacement(stringBuffer,"__");
            //System.out.println(matcher.group());
        }
        matcher.appendTail(stringBuffer);
        System.out.println(stringBuffer.toString());
        System.out.println("--------------------");
        String pattern2 = "\\[.*?\\]";
        Pattern compile2 = Pattern.compile(pattern2);
        Matcher matcher2 = compile2.matcher(content);
        while (matcher2.find()){
            System.out.println(matcher2.group());
        }
        Class<?> forName = Class.forName("top.sanguohf.egg.reflect.User");
        Field[] fields = forName.getDeclaredFields();
        for(Field field:fields){
            System.out.println(field.getGenericType().getTypeName());
        }
        //System.out.println(forName.getName());
    }

    public static void mainl(String[] args) throws ClassNotFoundException, NoSuchFieldException {
        EntityParams entityParams = new EntityParams();
        entityParams.setTableClassName("top.sanguohf.egg.reflect.UserClassesView");
        EntitySelectSql selectSql = new EntityParamParse(entityParams).parseToEntitySelectSql();
        System.out.println(selectSql.toSql());
    }
}
