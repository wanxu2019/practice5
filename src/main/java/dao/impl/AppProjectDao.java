package dao.impl;

import model.AppProject;
import utils.JDBCUtils;
import utils.NameUtils;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class AppProjectDao {

    ThreadLocal<String> tableName = new ThreadLocal<String>();

    public AppProjectDao(String appName) {
        tableName.set(NameUtils.toUnderScore(appName) + "_project");
        confirmTable();
    }

    /**
     * 确认表的信息，包括是否存在表，表的结构是否与实体结构对应
     * @return
     */
    public void confirmTable(){
        Set<String> set = new HashSet<String>(AppProject.getFieldMap().keySet());
        Connection conn = null;
        PreparedStatement ps =null;
        ResultSet rs = null;
        String sql = "show columns from " +tableName.get();
        boolean is  = false;
        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()){
                String colname = rs.getString("Field");
                if(set.contains(colname)) set.remove(colname);
            }
            is=true;
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            JDBCUtils.release(conn, ps, rs);
        }
        if(!is){
            System.out.println("查询表错误，说明无表，尝试新建表");
            createTable();
        }else {
            if(set.size()>0){
                System.out.println("表内结构有变，自动修改表结构");
                addColumns(new ArrayList<String>(set));
            }
        }
    }

    /**
     * 修改表格结构
     * @param fieldNames
     */
    public void addColumns(List<String> fieldNames){
        StringBuilder sb = new StringBuilder("ALTER TABLE ").append(tableName.get()).append(' ');
        Map<String,String> map = AppProject.getFieldMap();
        for (String s : fieldNames) {
            sb.append("ADD COLUMN ").append(s).append(' ').append(map.get(s)).append(',');
        }
        sb.deleteCharAt(sb.length()-1).append(';');
        if(doUpdate(sb.toString(),null)){
            System.out.println("修改表格结构成功，对应的SQL语句:"+sb.toString());
        }else {
            System.out.println("修改表格结构是被，具体错误查看日志");
        }
    }

    /**
     * 自动建表
     */
    public void createTable(){
        StringBuilder sb = new StringBuilder("CREATE TABLE `").append(tableName.get()).append("` (");
        Map<String,String> map = AppProject.getFieldMap();
        for (String fieldName : map.keySet()) {
            sb.append(fieldName).append(' ').append(map.get(fieldName)).append(',');
        }
        sb.append(AppProject.getPkString()).append(')').append(" ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8;");
        if(doUpdate(sb.toString(),null))
            System.out.println("新建表成功，表名:"+tableName.get());
        else  System.out.println("新建表失败，具体错误查看日志");
    }



    public List<AppProject> findListByUsername(String username) {
        String sql = "select * from " + this.tableName.get()  + " where username = ? ";
        List<Object> params = new ArrayList<Object>();
        params.add(0, username);
        return doQuery(sql, params);
    }

    public List<AppProject> findListByDomain(String domain) {
        String sql = "select * from " + this.tableName.get()  + " where  domain = ?";
        List<Object> params = new ArrayList<Object>();
        params.add(0, domain);
        return doQuery(sql, params);
    }


    public AppProject getOwnerById(int projectID, String username) {
        String sql = "select * from " + this.tableName.get() + " where  id = ? and username = ?";
        List<Object> params = new ArrayList<Object>();
        params.add(0, projectID);
        params.add(1, username);
        List<AppProject> appProjects =doQuery(sql,params);
        if(appProjects==null||appProjects.size()<1)
            return null;
        return appProjects.get(0);
    }
    public AppProject getProjectByKey(String key) {
        String sql = "select * from " + this.tableName.get() + " where resultKey = ?";
        List<Object> params = new ArrayList<Object>();
        params.add(0, key);
        List<AppProject> appProjects =doQuery(sql,params);
        if(appProjects==null||appProjects.size()<1)
            return null;
        return appProjects.get(0);
    }

    public void update(AppProject appProject, String username) {
        StringBuilder stringBuilder = new StringBuilder("UPDATE " + this.tableName.get()).append(" set ");
        List<Object> params = new ArrayList<Object>();
        if(appProject.getProjectName()!=null){
            stringBuilder.append("projectName =?,");
            params.add(appProject.getProjectName());

        }

        if(appProject.getMemo()!=null){
            stringBuilder.append("memo =?,");
            params.add( appProject.getMemo());
        }
        if(appProject.getAppResult()!=null){
            stringBuilder.append("appResult=?,");
            params.add(appProject.getAppResult());
        }
        if(appProject.getAppContent()!=null){
            stringBuilder.append("appContent=?,");
            params.add(appProject.getAppContent());
        }
        if(appProject.getReservation()!=null){
            stringBuilder.append("reservation=?,");
            params.add(appProject.getReservation());
        }
        if(appProject.getResultKey()!=null){
            stringBuilder.append("resultKey=?,");
            params.add(appProject.getResultKey());
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1).append(" where id=? and username = ?");
        params.add( appProject.getId());
        params.add(username);
        doUpdate(stringBuilder.toString(),params);
    }

    public void delete(int projectID, String username) {
        String sql = "delete from " + this.tableName.get() + " WHERE  id = ? AND username = ?";
        List<Object> params = new ArrayList<Object>();
        params.add(0, projectID);
        params.add(1, username);
        doUpdate(sql,params);
    }

    public AppProject add(AppProject appProject) {
        String sql = "INSERT INTO " + this.tableName.get() + "(projectName,username,memo,appResult,appContent,reservation) VALUES (?,?,?,?,?,?)";
        System.out.println("执行SQL查询语句"+sql);

        List<Object> params = new ArrayList<Object>();
        params.add(appProject.getProjectName());
        params.add( appProject.getUsername());
        params.add( appProject.getMemo());
        params.add( appProject.getAppResult());
        params.add( appProject.getAppContent());
        params.add(appProject.getReservation());
        System.out.println("执行更新的SQL语句"+sql);
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtils.getConnection();
            //3、创建命令执行对象
            ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            //4、执行
            if (params != null && params.size() > 0) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
            }
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            rs.next();
            appProject.setId(rs.getInt(1));
            System.out.println("appProject.getId() = " + appProject.getId());
        }catch (SQLException e) {
            e.printStackTrace();
        }finally {
            JDBCUtils.release(conn, ps, rs);
        }
        return appProject;
    }

    public List<AppProject> doQuery(String sql, List<Object> params) {
        System.out.println("执行SQL查询语句"+sql);
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<AppProject> result = new ArrayList<AppProject>();
        try {
            conn = JDBCUtils.getConnection();
            //3、创建命令执行对象
            ps = conn.prepareStatement(sql);
            //4、执行
            if (params != null && params.size() > 0) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new AppProject(
                        rs.getInt("id"),
                        rs.getString("projectName"),
                        (java.util.Date) rs.getObject("createTime"),
                        rs.getString("username"),
                        rs.getString("memo")==null?"":rs.getString("memo"),
                        rs.getString("appResult")==null?"":rs.getString("appResult"),
                        rs.getString("appContent")==null?"":rs.getString("appContent"),
                        rs.getString("reservation")==null?"":rs.getString("reservation"),
                        rs.getString("resultKey")==null?"":rs.getString("resultKey"),
                        (java.util.Date) rs.getObject("editTime")));
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return result;
        } finally {
            JDBCUtils.release(conn, ps, rs);
        }
    }

    public boolean doUpdate(String sql, List<Object> params){
        System.out.println("执行更新的SQL语句"+sql);
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtils.getConnection();
            //3、创建命令执行对象
            ps = conn.prepareStatement(sql);
            //4、执行
            if (params != null && params.size() > 0) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
            }
            ps.executeUpdate();
            return true;
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            JDBCUtils.release(conn, ps, rs);
        }

    }


}
