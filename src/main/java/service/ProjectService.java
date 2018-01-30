package service;

import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;
import dao.ITempLinkDao;
import dao.impl.AppProjectDaoImpl;
import dao.IAppProjectDao;
import dao.impl.TempLinkDaoImpl;
import model.AppProject;
import model.Result;
import model.TempLink;
import utils.ErrorCons;
import xjtucad.model.User;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;

public class ProjectService {
    private User userInfo = null;

    private IAppProjectDao projectDao = null;

    private ITempLinkDao tempLinkDao = null;

    public ProjectService(User userInfo, String appName) {
        this.userInfo = userInfo;
        this.projectDao = new AppProjectDaoImpl(appName);
        this.tempLinkDao = new TempLinkDaoImpl(appName);
    }

    public Result getProjectbyId(int projectID) {
        Result result = new Result();
        result.setState(false);
        try {
            AppProject appProject = projectDao.findOne(projectID);
            if (appProject == null) {
                result.setError(ErrorCons.NORESULT_ERROR);
            } else {
                if (authority(appProject, userInfo)) {
                    result.setState(true);
                    result.setContent(appProject);
                } else {
                    result.setError(ErrorCons.PERMISSION_ERROR);
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.setError(ErrorCons.DB_ERROR);
        } finally {
            return result;
        }
    }

    /**
     * 通过用户名获取相应的记录
     *
     * @param username 查询的用户名
     * @return Result对象
     */
    public Result getProjectListbyUsername(String username) {
        Result result = new Result();
        result.setState(false);
        if (username == null || username.length() < 1) {
            //如果没有填写用户名，默认查询当前用户名
            username = userInfo.getUsername();
        }
        try {
            List<AppProject> list = projectDao.findListByUsername(username, userInfo.getDomain());
            if (list == null || list.size() < 1 && !userInfo.getUsername().equals(username)) {
                result.setError(ErrorCons.PERMISSION_ERROR);
            } else {
                result.setState(true);
                result.setContent(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setError(ErrorCons.DB_ERROR);
        } finally {
            return result;
        }
    }

    /**
     * 通过当前用户的群组获取项目列表
     *
     * @return
     */
    public Result getProjectByDomain(String domain) {
        Result result = new Result();
        try {
            if (userInfo.getPermission().equals("superAdmin")) {
                //如果是超级管理员，返回根据传递的参数返回对应的值
                if (domain.equals("-1")) {
                    result.setContent(projectDao.findAll());
                } else {
                    result.setContent(projectDao.findListByDomain(domain));
                }
                result.setState(true);
            } else {
                if (userInfo.getPermission().equals("admin") && (domain.equals("-1") || userInfo.getDomain().equals(domain))) {
                    //如果是管理员，返回群组
                    result.setContent(projectDao.findListByDomain(userInfo.getDomain()));
                    result.setState(true);
                } else {
                    result.setError(ErrorCons.PERMISSION_ERROR);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setError(ErrorCons.DB_ERROR);
        } finally {
            return result;
        }
    }

    public Result getProjectByTempProjectID(String tempProjectID) {
        Result result = new Result();
        try {
            //获得对应的记录
            TempLink tempLink = tempLinkDao.findOne(tempProjectID);
            //查询到对应的appProject
            result = getProjectbyId(tempLink.getProjectID());
        } catch (Exception e) {
            e.printStackTrace();
            result.setError("数据库异常");
        }
        return result;
    }

    public Result newProjectRecord(String projectName, String memo, String appResult, String tempProjectID,
                                   String appContent,String reservation) {
        AppProject appProject = new AppProject();
        Result result  =new Result();

        appProject.setProjectName(projectName);
        appProject.setMemo(memo);
        appProject.setAppResult(appResult);
        appProject.setUsername(userInfo.getUsername());
        appProject.setDomain(userInfo.getDomain());
        appProject.setAppContent(appContent);
        appProject.setReservation(reservation);
        try {
            appProject = projectDao.add(appProject);
            if(tempProjectID!=null&&tempProjectID.length()>0){
                TempLink tempLink = tempLinkDao.findOne(tempProjectID);
                if(tempLink==null){
                    tempLink = new TempLink();
                    tempLink.setProjectID(appProject.getId());
                    tempLink.setTempProjectID(tempProjectID);
                    tempLinkDao.add(tempLink);
                }else {
                    tempLink.setProjectID(appProject.getId());
                    tempLinkDao.update(tempLink);
                }
            }
            result.setContent(appProject);
            result.setState(true);
        } catch (SQLException e) {
            result.setError(ErrorCons.DB_ERROR);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Result updateProjectRecord(int projectID,String projectName, String memo, String appResult,
                                      String tempProjectID,String appContent,String reservation){
        AppProject appProject = new AppProject();
        Result result  =new Result();
        appProject.setId(projectID);
        appProject.setProjectName(projectName);
        appProject.setMemo(memo);
        appProject.setAppResult(appResult);
        appProject.setAppContent(appContent);
        appProject.setReservation(reservation);
        try {
            AppProject old = projectDao.findOne(projectID);
            if(authority(old,userInfo)){
                //如果有修改的权限
                projectDao.update(appProject);
                if(tempProjectID!=null&&tempProjectID.length()>0){
                    TempLink tempLink = tempLinkDao.findOne(tempProjectID);
                    if(tempLink==null){
                        tempLink = new TempLink();
                        tempLink.setProjectID(appProject.getId());
                        tempLink.setTempProjectID(tempProjectID);
                        tempLinkDao.add(tempLink);
                    }else {
                        tempLink.setProjectID(appProject.getId());
                        tempLinkDao.update(tempLink);
                    }
                }
                result.setState(true);
            }else {
                result.setError(ErrorCons.PERMISSION_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setError(ErrorCons.DB_ERROR);
        }finally {
            return result;
        }
    }

    public Result deleteProjectRecord(int projectID){
        Result result  =new Result();
        try {
            AppProject appProject = projectDao.findOne(projectID);
            if(authority(appProject,userInfo)){
                projectDao.delete(projectID);
                List<TempLink> links = tempLinkDao.findListByProject(projectID);
                if(links!=null&&links.size()>0){
                    for (TempLink tempLink:links){
                        tempLinkDao.delete(tempLink.getId());
                    }
                }
                result.setState(true);
            }else {
                result.setError(ErrorCons.PERMISSION_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setError(ErrorCons.DB_ERROR);
        }finally {
            return result;
        }
    }
        /**
     * 权限认证函数
     *
     * @param project  项目
     * @param userInfo 用户信息
     * @return
     */
    boolean authority(AppProject project, User userInfo) {
        String permission = userInfo.getPermission();
        if (permission.equals("superAdmin")) {
            //如果用户是超级管理员
            return true;
        }
        if (permission.equals("admin")) {
            //如果用户是管理员
            if (userInfo.getDomain().equals(project.getDomain())) {
                return true;
            }
            return false;
        }
        if (userInfo.getUsername().equals(project.getUsername())) {
            return true;
        }
        return false;
    }



}