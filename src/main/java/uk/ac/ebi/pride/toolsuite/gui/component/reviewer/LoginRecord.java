package uk.ac.ebi.pride.toolsuite.gui.component.reviewer;


import uk.ac.ebi.pride.archive.web.service.model.project.ProjectDetailList;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class LoginRecord {
    private String userName;
    private char[] password;
    private ProjectDetailList projectDetailList;

    public LoginRecord(String userName, char[] password) {
        this(userName, password, null);
    }

    public LoginRecord(String userName, char[] password, ProjectDetailList projectDetailList) {
        this.userName = userName;
        this.password = password;
        this.projectDetailList = projectDetailList;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public ProjectDetailList getProjectDetailList() {
        return projectDetailList;
    }

    public void setProjectDetailList(ProjectDetailList projectDetailList) {
        this.projectDetailList = projectDetailList;
    }
}
