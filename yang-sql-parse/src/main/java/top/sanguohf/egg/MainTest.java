package top.sanguohf.egg;

import com.alibaba.fastjson.JSONObject;
import top.sanguohf.egg.base.EntityOrderBy;
import top.sanguohf.egg.constant.DbType;
import top.sanguohf.egg.ops.EntityPageSql;
import top.sanguohf.egg.ops.EntitySelectSql;
import top.sanguohf.egg.param.EntityParamParse;
import top.sanguohf.egg.param.EntityParams;

import java.util.LinkedList;
import java.util.List;

public class MainTest {

    public static void main1(String[] args) throws ClassNotFoundException, NoSuchFieldException {
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
        System.out.println(new EntityPageSql(selectSql).toPageSql(1,10, DbType.ORACLE));
        System.out.println(new EntityPageSql(selectSql).toCountSql(DbType.SQL));
        //ReflectEntity entity = new ReflectEntity();
        //System.out.println(entity.reflectSelectColumns(User.class));
    }

    public static void main(String[] args) throws ClassNotFoundException, NoSuchFieldException {
        JSONObject os=new JSONObject();
        os.put("userName","admin1");
        os.put("password","11122");
        os.put("id","99");
        EntityParams params=new EntityParams();
        params.setTableClassName("top.sanguohf.egg.reflect.User");
        params.setCondition(os);
        new EntityParamParse(params).parseToEntityInertSql();
        new EntityParamParse(params).parseToEntityUpdateSql();
        new EntityParamParse(params).parseToEntityDeleteSql();
    }
}
