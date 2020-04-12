package top.sanguohf.egg.test;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.sanguohf.egg.param.EntityParams;
import top.sanguohf.egg.test.entity.UserOne;
import top.sanguohf.top.bootcon.page.Page;
import top.sanguohf.top.bootcon.resp.CommonPageResp;
import top.sanguohf.top.bootcon.service.CommonService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestOne {

    @Autowired
    private CommonService commonService;

    //带条件查询
    @Test
    public void test4() throws NoSuchFieldException, IOException, ClassNotFoundException {
        EntityParams params = new EntityParams();
        params.setTableClassName("UserOne");
        //设置查询条件
        JSONObject con1 = new JSONObject();
        con1.put("left","userName");
        con1.put("right","%admin%");
        con1.put("relation","like");
        params.setCondition(con1);
        List list = commonService.findList(params);
        System.out.println(list);
        System.out.println("-----------------------");
        Page page = new Page();
        page.setPage(1);page.setSize(10);
        CommonPageResp pageList = commonService.findPageList(params, page);
        System.out.println(pageList);
    }

    @Test
    public void test3() throws NoSuchFieldException, IOException, ClassNotFoundException {
        EntityParams params = new EntityParams();
        params.setTableClassName("UserClassesView");
        //设置查询条件
        /*JSONObject con1 = new JSONObject();
        con1.put("left","userName");
        con1.put("right","%admin%");
        con1.put("relation","like");
        params.setCondition(con1);
        Page page = new Page();
        page.setPage(1);page.setSize(10);*/
        List list = commonService.findList(params);
    }
    //无条件查询
    @Test
    public void test1() throws Exception {
        UserOne one = new UserOne();
        one.setId("1234567");
        one.setPassword("kkkkk");
        one.setUserName("王五--1");
        UserOne one1 = new UserOne();
        one1.setId("12345678");
        one1.setPassword("kkkkk");
        one1.setUserName("王五--1");
        ArrayList objects = new ArrayList<>();
        objects.add(one);
        commonService.batchUpdate(objects);
        UserOne userOne = new UserOne();
        List list = commonService.findEntityList(userOne);
        System.out.println(list);
    }


    @Test
    public void test2() throws NoSuchFieldException, IOException, ClassNotFoundException {
        EntityParams params = new EntityParams();
        params.setTableClassName("UserOne");
        JSONObject con1 = new JSONObject();
        con1.put("userName","哈哈哈哈-9");
        con1.put("id","999000");
        con1.put("price",new BigDecimal(20.1));
        con1.put("blobOne",new Date());
        con1.put("bys","我是比爸爸".getBytes());
        params.setCondition(con1);
        commonService.delete(params);
        //List list = commonService.findList(params);
        //System.out.println(list);
    }
    @Test
    public void test5() throws NoSuchFieldException, IOException, ClassNotFoundException {
        EntityParams params = new EntityParams();
        params.setTableClassName("UserOne");
        JSONObject con1 = new JSONObject();
        //con1.put("userName","哈哈哈哈-1");
        con1.put("id","1998");
        params.setCondition(con1);
        //commonService.delete(params);
        Page page = new Page();
        page.setPage(1);
        page.setSize(10);
        CommonPageResp pageList = commonService.findPageList(params, page);
        System.out.println(pageList);
    }

    @Test
    public void test6() throws NoSuchFieldException, IOException, ClassNotFoundException {
        EntityParams params = new EntityParams();
        params.setTableClassName("Classes");
        /*JSONObject con1 = new JSONObject();
        con1.put("userName","哈哈哈哈-1");
        con1.put("id","1998");
        con1.put("password","88888");
        params.setCondition(con1);
        commonService.insert(params);*/
        List list = commonService.findList(params);
        System.out.println(list);
    }

    @Test
    public void test7() throws IOException {
        List<EntityParams> linkedList = new LinkedList<>();
        EntityParams params1 = new EntityParams();
        params1.setTableClassName("UserOne");
        JSONObject con1 = new JSONObject();
        con1.put("userName","哈哈哈哈-3");
        con1.put("id","1998-199");  //只会根据主键删除
        con1.put("password","88888-3-8");
        params1.setCondition(con1);

        EntityParams params2 = new EntityParams();
        params2.setTableClassName("UserOne");
        JSONObject con2 = new JSONObject();
        con2.put("password","88888-4-7");
        con2.put("id","19980999");//只会根据主键删除
        con2.put("userName","哈哈哈哈-7");
        params2.setCondition(con2);

        linkedList.add(params1);
        linkedList.add(params2);
        commonService.batchUpdate(linkedList);
    }

}
