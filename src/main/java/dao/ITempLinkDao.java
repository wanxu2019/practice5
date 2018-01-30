package dao;

import model.AppProject;
import model.TempLink;

import java.util.List;

public interface ITempLinkDao extends IBaseDao<TempLink> {

    List<TempLink> findAll();

    int delete(String tempProjectID) throws Exception;

    TempLink findOne(String tempProjectID);

    List<TempLink> findListByProject(int projectID);

}
