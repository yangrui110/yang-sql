package top.sanguohf.egg.test;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.sanguohf.egg.param.EntityParams;
import top.sanguohf.top.bootcon.page.Page;
import top.sanguohf.top.bootcon.resp.CommonPageResp;
import top.sanguohf.top.bootcon.service.CommonService;

import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestOne {

    @Autowired
    private CommonService commonService;

    @Test
    public void test1() throws NoSuchFieldException, IOException, ClassNotFoundException {
        EntityParams params = new EntityParams();
        params.setTableClassName("UserOne");
        //设置查询条件
        JSONObject con1 = new JSONObject();
        //con1.put("left","userName");
        //con1.put("right","%admin%");
        //con1.put("relation","like");
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
    public void test2() throws NoSuchFieldException, IOException, ClassNotFoundException {
        EntityParams params = new EntityParams();
        params.setTableClassName("UserOne");
        JSONObject con1 = new JSONObject();
        //con1.put("userName","哈哈哈哈");
        //con1.put("id","1001");
        params.setCondition(con1);
        commonService.update(params);
        List list = commonService.findList(params);
        System.out.println(list);
    }

}
