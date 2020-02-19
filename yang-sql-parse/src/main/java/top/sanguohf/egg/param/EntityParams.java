package top.sanguohf.egg.param;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import top.sanguohf.egg.base.EntityOrderBy;

import java.util.LinkedList;
import java.util.List;

@Data
public class EntityParams {
    private String tableClassName;

    /**
     * 传递的格式：
     *  {
     *      left:'userName',
     *      relation:'=',
     *      right: 'admin'
     *  }
     *  或者是更复杂的格式：
     *  {
     *      left: {
     *          condition: [
     *          {left: 'userName',relation: '=',right: 'admin'},
     *          {left: 'password',relation: '=',right: '123456'}
     *          ],combine: 'and'
     *      },
     *      relation: 'and',
     *      right: {
     *          condition: []
     *      }
     *  }
     *  又或者是List格式
     *  {
     *      condition: [],
     *      combine: 'and'
     *  }
     * */
    private JSONObject condition;
    private List<EntityOrderBy> orderBy;

    public List<EntityOrderBy> getOrderBy() {
        if(orderBy==null)
            orderBy=new LinkedList<>();
        return orderBy;
    }
}
