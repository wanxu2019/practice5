package model;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AppProject {
    /**
     * 项目id
     */
    private int id;
    /**
     * 项目名
     */
    private String projectName;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 用户名
     */
    private String username;
    /**
     * 备注
     */
    private String memo;
    /**
     * 项目结果，特指word片段
     */
    private String appResult;

    /**
     * 预留内容字段
     */
    private String appContent;
    /**
     * 预留内容字段2
     */
    private String reservation;

    private String resultKey;

    private Date editTime;


    private final static Map<String,String> fieldMap = new HashMap<String, String>(){
        {
            put("id","int(11) NOT NULL AUTO_INCREMENT COMMENT '当前项目ID'");
            put("projectName","varchar(255) DEFAULT NULL COMMENT '当前项目名，可以重复'");
            put("createTime","datetime DEFAULT CURRENT_TIMESTAMP COMMENT '当前项目创建时间'");
            put("username","varchar(255) DEFAULT NULL COMMENT '当前用户名'");
            put("memo"," varchar(255) DEFAULT NULL COMMENT '项目备注'");
            put("appResult","mediumtext COMMENT '项目报告结果'");
            put("appContent","text COMMENT '当前项目的内容'");
            put("reservation","text COMMENT '预留字段'");
            put("resultKey"," varchar(255) DEFAULT NULL COMMENT '记录密钥，用来获取共享数据' ");
            put("editTime"," datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '项目的更新时间' ");
        }
    };
    private final static String pkString = "PRIMARY KEY (`id`)";
    public AppProject() {
    }

    public AppProject(String projectName, String username, String memo, String appResult, String appContent, String reservation) {
        this.projectName = projectName;
        this.username = username;
        this.memo = memo;
        this.appResult = appResult;
        this.appContent = appContent;
        this.reservation = reservation;
    }

    public AppProject(int id, String projectName,  String username, String memo, String appResult, String appContent, String reservation,String resultKey) {
        this.id = id;
        this.projectName = projectName;
        this.username = username;
        this.memo = memo;
        this.appResult = appResult;
        this.appContent = appContent;
        this.reservation = reservation;
        this.resultKey=resultKey;
    }

    public AppProject(int id, String projectName, Date createTime, String username, String memo, String appResult, String appContent, String reservation,String resultKey,Date editTime) {
        this.id = id;
        this.projectName = projectName;
        this.createTime = createTime;
        this.username = username;
        this.memo = memo;
        this.appResult = appResult;
        this.appContent = appContent;
        this.reservation = reservation;
        this.resultKey = resultKey;
        this.editTime = editTime;
    }

    public static Map<String, String> getFieldMap() {
        return fieldMap;
    }

    public static String getPkString() {
        return pkString;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getAppResult() {
        return appResult;
    }

    public void setAppResult(String appResult) {
        this.appResult = appResult;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getAppContent() {
        return appContent;
    }

    public void setAppContent(String appContent) {
        this.appContent = appContent;
    }

    public String getReservation() {
        return reservation;
    }


    public void setReservation(String reservation) {
        this.reservation = reservation;
    }

    public String getResultKey() {
        return resultKey;
    }

    public void setResultKey(String resultKey) {
        this.resultKey = resultKey;
    }

    public Date getEditTime() {
        return editTime;
    }

    public void setEditTime(Date editTime) {
        this.editTime = editTime;
    }
}

