package dao.impl;

import dao.ITempLinkDao;
import model.Result;
import model.TempLink;

import java.util.ArrayList;
import java.util.List;
@Deprecated
public class TempLinkDaoImpl  extends BaseDaoImpl<TempLink>  implements ITempLinkDao {

    public TempLinkDaoImpl(String appName) {
        setTableName(appName+"_link");
    }
    public int delete(String tempProjectID) throws Exception {
        String sql = "delete * from "+this.tableName+" where tempProjectID = ? and domain = ?";
        List<Object> params = new ArrayList<Object>();
        params.add(0,tempProjectID);
        return excuteUpdate(sql,params);
    }

    public TempLink findOne(String tempProjectID) {
        String sql = "select * from "+this.tableName+ " where tempProjectID = ?";
        List<Object> params = new ArrayList<Object>();
        params.add(tempProjectID);
        List<TempLink> list = doQuery(sql,params);
        if(list==null||list.size()<1){
            return null;
        }
        return list.get(0);
    }

    public List<TempLink> findListByProject(int projectID) {
        String sql = "select * from "+this.tableName+ " where projectID = ?";
        List<Object> params = new ArrayList<Object>();
        params.add(projectID);
        List<TempLink> list = doQuery(sql,params);
        if(list==null||list.size()<1){
            return null;
        }
        return list;
    }

}
