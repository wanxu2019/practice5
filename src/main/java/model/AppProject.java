package model;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.sql.Date;
import java.sql.Timestamp;

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
    private Timestamp createDate;
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


    /**
     * 当前域
     */
    private String domain;

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

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
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
}

