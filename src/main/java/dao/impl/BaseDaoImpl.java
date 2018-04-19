package dao.impl;

import dao.IBaseDao;
import model.Result;
import utils.JDBCUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BaseDaoImpl<T> implements IBaseDao<T> {
    protected Class<T> clz =null;
    protected String tableName;


    protected String id = "id";
    Connection conn=null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    public BaseDaoImpl() {
        if(null == clz){
            clz = (Class<T>) (((ParameterizedType) (this.getClass()
                    .getGenericSuperclass()))
                    .getActualTypeArguments()[0]);
        }
        setTableName(clz.getName());
    }

//    @SuppressWarnings("unchecked")
//    private Class<T> getClz() {
//
//    }

    public void setTableName(String tableName){
        this.tableName =tableName ;
    }

    public T add(T t) throws SQLException, IllegalAccessException {
        Field[] fields = this.clz.getDeclaredFields();
        StringBuffer prefix = new StringBuffer("insert into "+this.tableName);
        StringBuffer left = new StringBuffer("(");
        StringBuffer right = new StringBuffer("(");
        List<Object> params = new ArrayList<Object>();
        Field tIDFile = null;
        for (Field field:fields) {
            field.setAccessible(true);
            if(field.getName().equals("id")){
                //如果当前属性是ID，跳过
                tIDFile = field;
                continue;
            }
            if(field.get(t)==null){
                continue;
            }
            left.append(field.getName()).append(',');
            right.append("?,");
            field.setAccessible(true);
            params.add(field.get(t));
        }
        //将最后一位多余的','修改为')'
        left.setCharAt(left.length()-1,')');
        right.setCharAt(right.length()-1,')');

        String sql = prefix.append(left).append("values").append(right).toString();
        System.out.println("拼凑得到的sql字符串："+sql);

        conn = JDBCUtils.getConnection();
        //3、创建命令执行对象
        ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
        if(params!=null && params.size()>0){
            for(int i=0;i<params.size();i++){
                ps.setObject(i+1, params.get(i));
            }
        }
        ps.executeUpdate();
        //获取自动生成的主键
        ResultSet id = ps.getGeneratedKeys();
        //设置对应的主键
        tIDFile.setAccessible(true);
        if(id.next()){
            tIDFile.set(t,id.getInt(1));
        }
        JDBCUtils.release(conn,ps,rs);
        return t;
    }

    public void delete(T t) throws SQLException {

    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM "+this.tableName+" WHERE id = ?";
        conn = JDBCUtils.getConnection();
        //3、创建命令执行对象
        ps = conn.prepareStatement(sql);
        ps.setObject(1,id);
        //4、执行
        ps.executeUpdate();
        JDBCUtils.release(conn,ps,rs);
    }

    public void update(T t) throws Exception {
        Field[] fields = this.clz.getDeclaredFields();
        Field tIDFile = null;
        T oldOne = null;
        StringBuffer prefix = new StringBuffer("UPDATE "+this.tableName+ " set ");
        List<Object> params = new ArrayList<Object>();
        for (Field field:fields) {
            if(field.getName().equals("id")){
                //如果当前属性是ID，跳过
                tIDFile = field;
                //首先获取到id属性
                tIDFile.setAccessible(true);
                int id = tIDFile.getInt(t);
                //查询到旧的
                oldOne = findOne(id);
                break;
            }

        }
        int count = 0;
        if(oldOne==null) throw new Exception("没有相应的");
        else {
            //如果查询到了以前的记录
            for (Field field:fields) {
                if(field.getName().equals("id")){
                    //如果当前属性是ID，跳过
                    continue;
                }
                field.setAccessible(true);
                if(field.get(t)!=null&&!(field.get(t).equals(field.get(oldOne)))){
                    count++;
                    //如果前后不同,拼接sql字符串
                    prefix.append(field.getName()).append(" = ? ,");
                    params.add(field.get(t));
                  

                }
            }
              prefix.setCharAt(prefix.length()-1,' ');
        }
        if(count==0) return;
        prefix.append("where id = ?");
        params.add(tIDFile.get(t));
        String sql = prefix.toString();
        System.out.println("拼凑得到的sql字符串："+sql);

        conn = JDBCUtils.getConnection();
        //3、创建命令执行对象
        ps = conn.prepareStatement(sql);
        if(params!=null && params.size()>0){
            for(int i=0;i<params.size();i++){
                ps.setObject(i+1, params.get(i));
            }
        }

        ps.executeUpdate();
        JDBCUtils.release(conn,ps,rs);
    }

    public T findOne(int id) {
        T result  = null;
        try{
            conn = JDBCUtils.getConnection();
            String sql = "select * from "+this.tableName+" WHERE "+this.id+" = ?";

            ps = conn.prepareStatement(sql);
            ps.setObject(1,id);
            rs = ps.executeQuery();
            if(rs.next()){
                result = getFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            JDBCUtils.release(conn,ps,rs);
            return result;
        }
    }


    public List<T> doQuery(String sql,List<Object> params) {
        List<T> result = new ArrayList<T>();
            try {
                conn = JDBCUtils.getConnection();
                //3、创建命令执行对象
                ps = conn.prepareStatement(sql);
                //4、执行
                if(params!=null && params.size()>0){
                    for(int i=0;i<params.size();i++){
                        ps.setObject(i+1, params.get(i));
                    }
                }
                rs = ps.executeQuery();
                while (rs.next()){
                    T obj = getFromResultSet(rs);
                    result.add(obj);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                JDBCUtils.release(conn,ps,rs);
                return result;
            }
    }

    public int excuteUpdate(String sql,List<Object> params) throws Exception {
       int result = 0;
        conn = JDBCUtils.getConnection();
        //3、创建命令执行对象
        ps = conn.prepareStatement(sql);
        //4、执行
        if(params!=null && params.size()>0){
            for(int i=0;i<params.size();i++){
                ps.setObject(i+1, params.get(i));
            }
        }
        result = ps.executeUpdate();
        JDBCUtils.release(conn,ps,rs);
        return result;
    }

    public List<T> findAll() {
        List<T> result = new ArrayList<T>();
        try {
            conn = JDBCUtils.getConnection();
            String sql = "select * from "+this.tableName;
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()){
                T obj = getFromResultSet(rs);
                result.add(obj);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            JDBCUtils.release(conn,ps,rs);
            return result;

        }
    }


    /**
     * 通过反射机制创建一个实例
     * @param rs
     * @return
     * @throws Exception
     */
    protected T getFromResultSet(ResultSet rs) throws Exception {
        T resultObject = this.clz.newInstance();
        ResultSetMetaData metaData  = rs.getMetaData();
        int colsLen = metaData.getColumnCount();
        for (int i = 0; i < colsLen; i++) {
            String colsName = metaData.getColumnName(i+1);
            Object colsValue = rs.getObject(colsName);
            if(colsValue==null){
                colsValue="";
            }
            Field field = this.clz.getDeclaredField(colsName);
            field.setAccessible(true);
            field.set(resultObject,colsValue);
        }
        return resultObject;
    }


}
