package dao;

import utils.JDBCUtils;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
 public interface IBaseDao<T>{
    //添加
    public T add(T t) throws SQLException, IllegalAccessException;
    //删除
    public void delete(int id) throws SQLException;
    //更新
    public void update(T t) throws Exception;

    //根据id查询
    public T findOne(int id);

    public  List<T> doQuery(String sql,List<Object> params);

    //查询所有
    public List<T> findAll();

}