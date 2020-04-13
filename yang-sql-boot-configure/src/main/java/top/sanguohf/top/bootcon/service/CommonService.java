package top.sanguohf.top.bootcon.service;

import top.sanguohf.egg.param.EntityParams;
import top.sanguohf.top.bootcon.page.Page;
import top.sanguohf.top.bootcon.resp.CommonPageResp;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface CommonService {
    /** @param params
     * @param page
     * */
    CommonPageResp findPageList(EntityParams params, Page page) throws ClassNotFoundException, NoSuchFieldException, IOException, InvocationTargetException, IllegalAccessException;
    /** @param params
     * */
    List findList(EntityParams params) throws ClassNotFoundException, NoSuchFieldException, IOException, InvocationTargetException, IllegalAccessException;
  /** @param params
     *  */
    long count(EntityParams params) throws ClassNotFoundException, NoSuchFieldException, IOException, InvocationTargetException, IllegalAccessException;
    /** @param paramData
     * */
    void insert(EntityParams paramData) throws ClassNotFoundException, NoSuchFieldException, IOException, InvocationTargetException, IllegalAccessException;
    /** @param paramsData
     * */
    void update(EntityParams paramsData) throws ClassNotFoundException, NoSuchFieldException, IOException, InvocationTargetException, IllegalAccessException;
    /** @param paramsData
     **/
    void delete(EntityParams paramsData) throws ClassNotFoundException, NoSuchFieldException, IOException, InvocationTargetException, IllegalAccessException;
    /** @param params
     * */
    void batchInsert(List<EntityParams> params) throws IOException, InvocationTargetException, IllegalAccessException;
    /** @param params
     *  */
    void batchUpdate(List<EntityParams> params) throws Exception;
    /** @param params
     * */
    void batchDelete(List<EntityParams> params) throws IOException, InvocationTargetException, IllegalAccessException;
    /**
     * 批量保存方法，1、主键存在，则更新 2、主键不存在，则插入
     * */
   void batchSave(List<EntityParams> params) throws Exception;

  <T> CommonPageResp<List<T>> findEntityPageList(T data, Class<T> tClass, Page page) throws Exception;

    <T> List<T> findEntityList(T params,Class<T> tClass) throws Exception;
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
    <T> void batchEntityInsert(List<T> params) throws IOException, IllegalAccessException, InvocationTargetException;
    /** @param params
     *  */
    <T> void batchEntityUpdate(List<T> params) throws Exception;

    <T> void batchEntityDelete(List<T> params) throws Exception;

    /**
     * 根据主键查找到对应的值
     * 适用于主键数目为单个
     * */
    <T> T findByPrimaryKey(Class<T> tClass,Object key) throws NoSuchFieldException, ClassNotFoundException, IOException, Exception;
    /**
     * 根据主键查找到对应的值
     * 适用于主键数目为单个
     * */
    <T> List<T> findByPrimaryKeys(Class<T> tClass, List<? extends Object> keys) throws NoSuchFieldException, ClassNotFoundException, IOException, Exception;

    <T> void batchSaveEntity(List<T> entitys) throws Exception;
}
