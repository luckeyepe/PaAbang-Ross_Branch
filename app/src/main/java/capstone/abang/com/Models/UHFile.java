package capstone.abang.com.Models;

/**
 * Created by Pc-user on 09/01/2018.
 */

public class UHFile {
    private String UHUsercode;
    private String UHUsername;
    private String UHPassword;
    private String UHStatus;
    private String uhdatecreated;
    private String uhimageqr;

    public UHFile() {

    }

    public UHFile(String UHUsercode, String UHUsername, String UHPassword, String UHStatus, String uhdatecreated, String uhimageqr) {
        this.UHUsercode = UHUsercode;
        this.UHUsername = UHUsername;
        this.UHPassword = UHPassword;
        this.UHStatus = UHStatus;
        this.uhdatecreated = uhdatecreated;
        this.uhimageqr = uhimageqr;
    }

    public String getUHUsercode() {
        return UHUsercode;
    }

    public void setUHUsercode(String UHUsercode) {
        this.UHUsercode = UHUsercode;
    }

    public String getUHUsername() {
        return UHUsername;
    }

    public void setUHUsername(String UHUsername) {
        this.UHUsername = UHUsername;
    }

    public String getUHPassword() {
        return UHPassword;
    }

    public void setUHPassword(String UHPassword) {
        this.UHPassword = UHPassword;
    }

    public String getUHStatus() {
        return UHStatus;
    }

    public void setUHStatus(String UHStatus) {
        this.UHStatus = UHStatus;
    }

    public String getUhdatecreated() {
        return uhdatecreated;
    }

    public void setUhdatecreated(String uhdatecreated) {
        this.uhdatecreated = uhdatecreated;
    }

    public String getUhimageqr() {
        return uhimageqr;
    }

    public void setUhimageqr(String uhimageqr) {
        this.uhimageqr = uhimageqr;
    }
}
