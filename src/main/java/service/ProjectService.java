package service;

import com.sun.deploy.ref.AppRef;
import dao.impl.AppProjectDao;
import model.AppProject;
import model.Result;
import utils.ErrorCons;

import java.util.ArrayList;
import java.util.List;

public class ProjectService {

    private AppProjectDao projectDao = null;

    public ProjectService(String appName) {
        this.projectDao = new AppProjectDao(appName);
    }

    public Result getAppProjectbyId(int projectID,String username) {
        Result result = new Result();
        result.setState(false);
        try {
            //得到查询结果
            AppProject appProject = projectDao.getOwnerById(projectID,username);
            if (appProject == null) {
                //无结果
                result.setError(ErrorCons.NORESULT_ERROR);
            } else {
                //权限正确，返回正确结果
                result.setState(true);
                result.setContent(appProject);
            }
            return result;
        } catch (Exception e) {
//            数据库错误
            e.printStackTrace();
            result.setError(ErrorCons.DB_ERROR);
            return result;
        }
    }

    public Result getAppProjectByKey(String keys){
        Result result = new Result();
        result.setState(false);
        try {
            List<AppProject> appProjectList = new ArrayList<AppProject>();
            //得到查询结果
            String[] resultKeyArr = keys.split(";");
            for (String s : resultKeyArr) {
                AppProject appProject = projectDao.getProjectByKey(s);
                if(appProject!=null)
                    appProjectList.add(appProject);
            }
            if (resultKeyArr == null) {
                //无结果
                result.setError(ErrorCons.NORESULT_ERROR);
            } else {
                //权限正确，返回正确结果
                result.setState(true);
                result.setContent(appProjectList);
            }
            return result;
        } catch (Exception e) {
//            数据库错误
            e.printStackTrace();
            result.setError(ErrorCons.DB_ERROR);
            return result;
        }
    }
    /**
     * 通过用户名获取相应的记录
     * @return Result对象
     */
    public Result getAppProjectList(String username) {
        Result result = new Result();
        result.setState(false);
        //从用户信息中获取用户名
        try {
            List<AppProject> list = projectDao.findListByUsername(username);
            if (list == null) {
                result.setError(ErrorCons.NORESULT_ERROR);
            } else {
                result.setState(true);
                result.setContent(list);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.setError(ErrorCons.DB_ERROR);
            return result;
        }
    }



    public Result newProjectRecord(AppProject appProject) {
        Result result  =new Result();
        appProject = projectDao.add(appProject);
        result.setContent(appProject);
        result.setState(true);
        return result;
    }

    public Result updateProjectRecord(AppProject appProject,String username){
        Result result  =new Result();
        try {
            projectDao.update(appProject,username);
            result.setState(true);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.setError(ErrorCons.DB_ERROR);
            return result;
        }
    }
    public Result updateProjectRecord(String oldKey,int newID,String resultKey,String username){
        Result result = new Result();
        try {
            AppProject appProject = projectDao.getProjectByKey(oldKey);
            if(appProject!=null) {
                appProject.setResultKey("");
                projectDao.update(appProject,appProject.getUsername());
            }
            appProject = projectDao.getOwnerById(newID,username);
            if(appProject!=null){
                appProject.setResultKey(resultKey);
                projectDao.update(appProject,username);
            }
            result.setState(true);
            return result;

        }catch (Exception e) {
            e.printStackTrace();
            result.setError(ErrorCons.DB_ERROR);
            return result;

        }
    }

    public Result deleteProjectRecord(int projectID,String username){
        Result result  =new Result();
        try {
            projectDao.delete(projectID,username);
            result.setState(true);
        } catch (Exception e) {
            e.printStackTrace();
            result.setError(ErrorCons.DB_ERROR);
        }finally {
            return result;
        }
    }

//        /**
//     * 权限认证函数
//     *
//     * @param project  项目
//     * @param userInfo 用户信息
//     * @return
//     */
//        @Deprecated
//    boolean authority(AppProject project, User userInfo) {
//        String permission = userInfo.getPermission();
//        if (permission.equals("superAdmin")) {
//            //如果用户是超级管理员
//            return true;
//        }
//        if (permission.equals("admin")) {
//            //如果用户是管理员
//            if (userInfo.getDomain().equals(project.getDomain())) {
//                return true;
//            }
//            return false;
//        }
//        if (userInfo.getUsername().equals(project.getUsername())) {
//            return true;
//        }
//        return false;
//    }
//
//

}