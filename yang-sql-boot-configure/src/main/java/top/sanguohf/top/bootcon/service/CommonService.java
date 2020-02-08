package top.sanguohf.top.bootcon.service;

import top.sanguohf.egg.param.EntityParams;
import top.sanguohf.top.bootcon.page.Page;
import top.sanguohf.top.bootcon.resp.CommonPageResp;
import java.io.IOException;
import java.util.List;

public interface CommonService {
    /** @param params
     * @param page
     * */
    CommonPageResp findPageList(EntityParams params, Page page) throws ClassNotFoundException, NoSuchFieldException, IOException;
    /** @param params
     * */
    List findList(EntityParams params) throws ClassNotFoundException, NoSuchFieldException, IOException;
    /** @param params
     *  */
    long count(EntityParams params) throws ClassNotFoundException, NoSuchFieldException, IOException;
    /** @param paramData
     * */
    void insert(EntityParams paramData) throws ClassNotFoundException, NoSuchFieldException, IOException;
    /** @param paramsData
     * */
    void update(EntityParams paramsData) throws ClassNotFoundException, NoSuchFieldException, IOException;
    /** @param paramsData
     **/
    void delete(EntityParams paramsData) throws ClassNotFoundException, NoSuchFieldException, IOException;
    /** @param params
     * */
    void batchInsert(List<EntityParams> params) throws IOException;
    /** @param params
     *  */
    void batchUpdate(List<EntityParams> params) throws IOException;
    /** @param params
     * */
    void batchDelete(List<EntityParams> params) throws IOException;
}
