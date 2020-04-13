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
    void batchUpdate(List<EntityParams> params) throws Exception;
    /** @param params
     * */
    void batchDelete(List<EntityParams> params) throws IOException;

    <T> CommonPageResp<T> findEntityPageList(T data, Page page) throws Exception;

    <T> List findEntityList(T params) throws Exception;
    /** @param params
     *  */
    <T> long countEntity(T params) throws Exception;
    /** @param paramData
     * */
    <T> void insertEntity(T paramData) throws Exception;
    /** @param paramsData
     * */
    <T> void updateEntity(T paramsData) throws Exception;
    /** @param paramsData
     **/
    <T> void deleteEntity(T paramsData) throws Exception;
    /** @param params
     * */
    <T> void batchEntityInsert(List<T> params) throws IOException, IllegalAccessException;
    /** @param params
     *  */
    <T> void batchEntityUpdate(List<T> params) throws Exception;
}
