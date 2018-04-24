package controller;

import com.alibaba.fastjson.JSON;
import model.AppProject;
import model.Result;
import service.ProjectService;
import utils.ErrorCons;
import xjtucad.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "GetDataAPI", urlPatterns = {"/api/v1/project"})
public class ProjectServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession httpSession = req.getSession();
        User userinfo = (User) httpSession.getAttribute("userInfo");
        Result result = new Result();
        String projectID = req.getParameter("id");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json,charset=UTF-8");
//        resp.getWriter().print(JSON.toJSONString(result));
        boolean notEnd = true;
        try {

            String toolName = req.getParameter("toolName");
            if (toolName == null || toolName.length() == 0) {
                String[] strings = req.getHeader("referer").split("/");
                toolName = strings[3];
            }

            ProjectService projectService = new ProjectService(toolName);
//            if (toolName == null || toolName.length() < 1) {
//                result.setContent("请传入toolName参数");
//                notEnd = false;
//            }

            if (projectID != null && projectID.length() > 0) {
                //根据projectID查询
                int id = Integer.valueOf(projectID);
                result = projectService.getAppProjectbyId(id,userinfo.getUsername());
                notEnd = false;
            }


//            String domainAll = req.getParameter("domainAll");
//            String domain = req.getParameter("domain");
//            if (notEnd && domain != null && domain.length() > 0) {
//                //根据群组查询
//                result = projectService.get(domain);
//                notEnd = false;
//            }
//
//            String tempProjectID = req.getParameter("tempProjectID");
//            if (notEnd && tempProjectID != null && tempProjectID.length() > 0) {
//                //根据模板ID查询
//                result = projectService.getProjectByTempProjectID(tempProjectID);
//                notEnd = false;
//            }

            if (notEnd) {
                //根据userAll查询
                result = projectService.getAppProjectList(userinfo.getUsername());
                notEnd = false;
            }

            if (notEnd) {
                result.setContent(ErrorCons.PARAMS_ERROR);
            }
        } catch (Exception e) {
            result.setContent(ErrorCons.PARAMS_ERROR);
        } finally {
            resp.getWriter().print(JSON.toJSONString(result));
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin:", "*");
        resp.addHeader("Access-Control-Allow-Methods", "POST,GET,PUT,DELETE");
        resp.addHeader("Access-Control-Allow-Headers", "Content-Type,Authorization");
        HttpSession httpSession = req.getSession();
        User userinfo = (User) httpSession.getAttribute("userInfo");
        Result result = new Result();
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json,charset=UTF-8");
        //获取对应的参数
        String toolName = req.getParameter("toolName");
        String projectName = req.getParameter("projectName");

        if (toolName == null || toolName.length() == 0) {
            String[] strings = req.getHeader("referer").split("/");
            toolName = strings[3];
        }

        //判断参数是否正确
        if (toolName == null || toolName.length() < 1 || projectName == null || projectName.length() < 1) {
            result.setContent(ErrorCons.PARAMS_ERROR);
        } else {
            //如果基本参数没有问题，转到服务
            ProjectService projectService = new ProjectService(toolName);
            result = projectService.newProjectRecord(
                    new AppProject(
                            req.getParameter("projectName"),
                            userinfo.getUsername(),
                            req.getParameter("appResult"),
                            req.getParameter("tempProjectID"),
                            req.getParameter("appContent"),
                            req.getParameter("reservation"))
            );
        }
        resp.getWriter().print(JSON.toJSONString(result));

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.addHeader("Access-Control-Allow-Origin:", "*");
        resp.addHeader("Access-Control-Allow-Methods", "POST,GET,PUT,DELETE");
        resp.addHeader("Access-Control-Allow-Headers", "Content-Type,Authorization");

        HttpSession httpSession = req.getSession();
        req.setCharacterEncoding("utf-8");
        User userinfo = (User) httpSession.getAttribute("userInfo");
        Result result = new Result();
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json,charset=UTF-8");
        BufferedReader in = new BufferedReader(new InputStreamReader(req.getInputStream()));
        String line;
        Map<String, String> params = new HashMap<String, String>();
        while ((line = in.readLine()) != null)
            params.putAll(getPraFromReq(line));
        try {
            //获取要更新的项目ID
            int projectID = Integer.parseInt(params.get("id"));
//           获取对应的toolName
            String toolName = params.get("toolName");
            if (toolName == null || toolName.length() == 0) {
                String[] strings = req.getHeader("referer").split("/");
                toolName = strings[3];
            }
            ProjectService projectService = new ProjectService(toolName);

            result = projectService.updateProjectRecord(
                    new AppProject(projectID,
                            params.get("projectName"),
                            userinfo.getUsername(),
                            params.get("memo"),
                            params.get("appResult"),
                            params.get("appContent"),
                            params.get("reservation"))
            ,userinfo.getUsername());

        } catch (Exception e) {
            e.printStackTrace();
            result.setError(ErrorCons.PARAMS_ERROR);
        } finally {
            resp.getWriter().println(JSON.toJSONString(result));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin:", "*");
        resp.addHeader("Access-Control-Allow-Methods", "POST,GET,PUT,DELETE");
        resp.addHeader("Access-Control-Allow-Headers", "Content-Type,Authorization");

        HttpSession httpSession = req.getSession();
        User userinfo = (User) httpSession.getAttribute("userInfo");
        Result result = new Result();
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json,charset=UTF-8");

        BufferedReader in = new BufferedReader(new InputStreamReader(req.getInputStream()));
        String line;
        Map<String, String> params = new HashMap<String, String>();
        while ((line = in.readLine()) != null)
            params.putAll(getPraFromReq(line));
        try {
            //获取要更新的项目ID
            String toolName = params.get("toolName");
            if (toolName == null || toolName.length() == 0) {
                String[] strings = req.getHeader("referer").split("/");
                toolName = strings[3];
            }
            int projectID = Integer.parseInt(params.get("id"));
            ProjectService projectService = new ProjectService(toolName);
            result = projectService.deleteProjectRecord(projectID,userinfo.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
            result.setError(ErrorCons.PARAMS_ERROR);
        } finally {
            resp.getWriter().println(JSON.toJSONString(result));
        }
    }

    private Map<String, String> getPraFromReq(String string) throws UnsupportedEncodingException {
        Map<String, String> mapRequest = new HashMap<String, String>();
        String[] arrResult = string.split("&");
        if (arrResult != null && arrResult.length > 0) {
            for (String str : arrResult) {
                String[] p = str.split("=");
                if (p.length == 2 && p[0] != null && p[0].length() > 0 && p[1] != null && p[1].length() > 0) {
                    mapRequest.put(p[0], URLDecoder.decode(p[1], "UTF-8"));
                }
            }
        }
        return mapRequest;
    }
}
