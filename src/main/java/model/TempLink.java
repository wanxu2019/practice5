package model;
@Deprecated
public class TempLink {
    private int id;
    private String tempProjectID;
    private int projectID;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTempProjectID() {
        return tempProjectID;
    }

    public void setTempProjectID(String tempProjectID) {
        this.tempProjectID = tempProjectID;
    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }
}
