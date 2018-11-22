package capstone.abang.com.Models;

/**
 * Created by Pc-user on 09/01/2018.
 */

public class UDFile {
    private String UDUserCode;
    private String UDFullname;
    private String UDAddr;
    private String UDEmail;
    private String UDStatus;
    private String UDUserType;
    private String UDContact;
    private String UDImageNbi;
    private String UDImageProfile;
    private String UDImageSecondaryID;
    private String UDApplicationStatus;
    private int UDTransactions;
    private String UDNotified;
    private String udDocuStatus;
    private String udCompanyID;



    public UDFile() {

    }

    public UDFile(String UDUserCode, String UDFullname, String UDAddr, String UDEmail, String UDStatus, String UDUserType, String UDContact, String UDImageNbi, String UDImageProfile, String UDImageSecondaryID, String UDApplicationStatus, int UDTransactions) {
        this.UDUserCode = UDUserCode;
        this.UDFullname = UDFullname;
        this.UDAddr = UDAddr;
        this.UDEmail = UDEmail;
        this.UDStatus = UDStatus;
        this.UDUserType = UDUserType;
        this.UDContact = UDContact;
        this.UDImageNbi = UDImageNbi;
        this.UDImageProfile = UDImageProfile;
        this.UDImageSecondaryID = UDImageSecondaryID;
        this.UDApplicationStatus = UDApplicationStatus;
        this.UDTransactions = UDTransactions;
    }

    public String getUdCompanyID() {
        return udCompanyID;
    }

    public void setUdCompanyID(String udCompanyID) {
        this.udCompanyID = udCompanyID;
    }

    public String getUdDocuStatus() {
        return udDocuStatus;
    }

    public void setUdDocuStatus(String udDocuStatus) {
        this.udDocuStatus = udDocuStatus;
    }

    public String getUDNotified() {
        return UDNotified;
    }

    public void setUDNotified(String UDNotified) {
        this.UDNotified = UDNotified;
    }

    public int getUDTransactions() {
        return UDTransactions;
    }

    public void setUDTransactions(int UDTransactions) {
        this.UDTransactions = UDTransactions;
    }

    public String getUDUserCode() {
        return UDUserCode;
    }

    public void setUDUserCode(String UDUserCode) {
        this.UDUserCode = UDUserCode;
    }

    public String getUDFullname() {
        return UDFullname;
    }

    public void setUDFullname(String UDFullname) {
        this.UDFullname = UDFullname;
    }

    public String getUDAddr() {
        return UDAddr;
    }

    public void setUDAddr(String UDAddr) {
        this.UDAddr = UDAddr;
    }

    public String getUDEmail() {
        return UDEmail;
    }

    public void setUDEmail(String UDEmail) {
        this.UDEmail = UDEmail;
    }

    public String getUDStatus() {
        return UDStatus;
    }

    public void setUDStatus(String UDStatus) {
        this.UDStatus = UDStatus;
    }

    public String getUDUserType() {
        return UDUserType;
    }

    public void setUDUserType(String UDUserType) {
        this.UDUserType = UDUserType;
    }

    public String getUDContact() {
        return UDContact;
    }

    public void setUDContact(String UDContact) {
        this.UDContact = UDContact;
    }

    public String getUDImageNbi() {
        return UDImageNbi;
    }

    public void setUDImageNbi(String UDImageNbi) {
        this.UDImageNbi = UDImageNbi;
    }

    public String getUDImageProfile() {
        return UDImageProfile;
    }

    public void setUDImageProfile(String UDImageProfile) {
        this.UDImageProfile = UDImageProfile;
    }

    public String getUDImageSecondaryID() {
        return UDImageSecondaryID;
    }

    public void setUDImageSecondaryID(String UDImageSecondaryID) {
        this.UDImageSecondaryID = UDImageSecondaryID;
    }

    public String getUDApplicationStatus() {
        return UDApplicationStatus;
    }

    public void setUDApplicationStatus(String UDApplicationStatus) {
        this.UDApplicationStatus = UDApplicationStatus;
    }
}
