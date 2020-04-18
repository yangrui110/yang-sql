package top.sanguohf.top.bootcon.service;

import com.alibaba.fastjson.JSONObject;
import top.sanguohf.egg.base.EntityOrderBy;
import top.sanguohf.egg.param.EntityParams;
import top.sanguohf.top.bootcon.page.Page;
import top.sanguohf.top.bootcon.resp.CommonPageResp;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface CommonService {
    /**
     * @param params
     * @param page
     */
    CommonPageResp findPageList(EntityParams params, Page page) throws ClassNotFoundException, NoSuchFieldException, IOException, InvocationTargetException, IllegalAccessException;

    /**
     * @param params
     */
    List findList(EntityParams params) throws ClassNotFoundException, NoSuchFieldException, IOException, InvocationTargetException, IllegalAccessException;

    /**
     * @param params
     */
    long count(EntityParams params) throws ClassNotFoundException, NoSuchFieldException, IOException, InvocationTargetException, IllegalAccessException;

    /**
     * @param paramData
     */
    void insert(EntityParams paramData) throws ClassNotFoundException, NoSuchFieldException, IOException, InvocationTargetException, IllegalAccessException;

    /**
     * @param paramsData
     */
    void update(EntityParams paramsData) throws ClassNotFoundException, NoSuchFieldException, IOException, InvocationTargetException, IllegalAccessException;

    /**
     * @param paramsData
     **/
    void delete(EntityParams paramsData) throws ClassNotFoundException, NoSuchFieldException, IOException, InvocationTargetException, IllegalAccessException;

    /**
     * @param params
     */
    void batchInsert(List<EntityParams> params) throws IOException, InvocationTargetException, IllegalAccessException;

    /**
     * @param params
     */
    void batchUpdate(List<EntityParams> params) throws Exception;

    /**
     * @param params
     */
    void batchDelete(List<EntityParams> params) throws IOException, InvocationTargetException, IllegalAccessException;

    /**
     * 批量保存方法，1、主键存在，则更新 2、主键不存在，则插入
     */
    void batchSave(List<EntityParams> params) throws Exception;

    <T,E> CommonPageResp<List<E>> findByEntityPageList(T data, Class<E> toJavaBean, Page page) throws Exception;

    <T,E> CommonPageResp<List<E>> findByEntityPageList(T data, Class<E> toJavaBean,List<EntityOrderBy> orderBys, Page page) throws Exception;

    <T,E> CommonPageResp<List<E>> findEntityPageList(Class<T> viewClass,Class<E> toJavaBean,  Page page) throws Exception;

    /**
     * 根据condition查找数据
     */
    <T,E> CommonPageResp<List<E>> findEntityPageList(Class<T> viewClass,Class<E> toJavaBean, JSONObject condition, Page page) throws Exception;

    /**
     * 增加排序字段
     */
    <T,E> CommonPageResp<List<E>> findEntityPageList(Class<T> viewClass,Class<E> toJavaBean, JSONObject condition, List<EntityOrderBy> orderBys, Page page) throws Exception;

    <T,E> CommonPageResp<List<E>> findEntityPageList(Class<T> viewClass,Class<E> toJavaBean, List<EntityOrderBy> orderBys, Page page) throws Exception;

    <T,E> List<E> findListByEntity(T data,Class<E> toJavaBean) throws Exception;

    <T,E> List<E> findListByEntity(T data,Class<E> toJavaBean,  List<EntityOrderBy> orderBys) throws Exception;
    /**
     * 根据condition查找数据
     */
    <T,E> List<E> findEntityList(Class<T> viewClass,Class<E> toJavaBean) throws Exception;

    <T,E> List<E> findEntityList(Class<T> viewClass,Class<E> toJavaBean, JSONObject condition) throws Exception;

    <T,E> List<E> findEntityList(Class<T> viewClass,Class<E> toJavaBean, JSONObject condition, List<EntityOrderBy> orderBys) throws Exception;

    <T,E> List<E> findEntityList(Class<T> viewClass,Class<E> toJavaBean, List<EntityOrderBy> orderBys) throws Exception;

    /**
     * @param params
     */
    <T> long countEntity(T params) throws Exception;

    /**
     * @param paramData
     */
    <T> void insertEntity(T paramData) throws Exception;

    /**
     * @param paramsData
     */
    <T> void updateEntity(T paramsData) throws Exception;

    /**
     * @param paramsData
     **/
    <T> void deleteEntity(T paramsData) throws Exception;

    /**
     * @param params
     */
    <T> void batchEntityInsert(List<T> params) throws IOException, IllegalAccessException, InvocationTargetException;

    /**
     * @param params
     */
    <T> void batchEntityUpdate(List<T> params) throws Exception;

    <T> void batchEntityDelete(List<T> params) throws Exception;

    /**
     * 根据主键查找到对应的值
     * 适用于主键数目为单个
     */
    <T,E> E findByPrimaryKey(Class<T> viewClass,Class<E> toJavaBean , Object key) throws NoSuchFieldException, ClassNotFoundException, IOException, Exception;

    /**
     * 根据主键查找到对应的值
     * 适用于主键数目为单个
     */
    <T,E> List<E> findByPrimaryKeys(Class<T> viewClass,Class<E> toJavaBean, List<? extends Object> keys) throws NoSuchFieldException, ClassNotFoundException, IOException, Exception;

    <T> void batchSaveEntity(List<T> entitys) throws Exception;
}
