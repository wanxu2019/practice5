package dao.impl;

import dao.IAppProjectDao;
import dao.impl.BaseDaoImpl;
import model.AppProject;
import utils.JDBCUtils;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppProjectDaoImpl extends BaseDaoImpl<AppProject> implements IAppProjectDao {

    public AppProjectDaoImpl(String appName) {
        setTableName(appName+"_project");
    }

    public List<AppProject> findListByUsername(String username, String domain) {
       String sql = "select * from "+this.tableName + " where username = ? and domain = ?";
       List<Object> params = new ArrayList<Object>();
       params.add(0,username);
       params.add(1,domain);
       return doQuery(sql,params);
    }

    public List<AppProject> findListByDomain(String domain) {
        String sql = "select * from "+this.tableName + " where  domain = ?";
        List<Object> params = new ArrayList<Object>();
        params.add(0,domain);
        return doQuery(sql,params);
    }

}
