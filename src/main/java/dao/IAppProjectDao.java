package dao;

import model.AppProject;

import java.util.List;

public interface IAppProjectDao extends IBaseDao<AppProject> {

    List<AppProject> findListByUsername(String username,String domain);

    List<AppProject> findListByDomain(String domain);

    List<AppProject> findAll();


}
