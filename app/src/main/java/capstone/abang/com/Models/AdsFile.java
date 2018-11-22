package capstone.abang.com.Models;

/**
 * Created by Salarda on 23/01/2018.
 */

public class AdsFile {
    private String AdCode;
    private String AdCompBranch;
    private String AdCompany;
    private String AdContact;
    private String AdDesc;
    private String AdImage;
    private String AdStatus;

    public AdsFile(String adCode, String adCompBranch, String adCompany, String adContact, String adDesc, String adImage, String adStatus) {
        AdCode = adCode;
        AdCompBranch = adCompBranch;
        AdCompany = adCompany;
        AdContact = adContact;
        AdDesc = adDesc;
        AdImage = adImage;
        AdStatus = adStatus;
    }

    public AdsFile(){}

    public String getAdCode() {
        return AdCode;
    }

    public void setAdCode(String adCode) {
        AdCode = adCode;
    }

    public String getAdCompBranch() {
        return AdCompBranch;
    }

    public void setAdCompBranch(String adCompBranch) {
        AdCompBranch = adCompBranch;
    }

    public String getAdCompany() {
        return AdCompany;
    }

    public void setAdCompany(String adCompany) {
        AdCompany = adCompany;
    }

    public String getAdContact() {
        return AdContact;
    }

    public void setAdContact(String adContact) {
        AdContact = adContact;
    }

    public String getAdDesc() {
        return AdDesc;
    }

    public void setAdDesc(String adDesc) {
        AdDesc = adDesc;
    }

    public String getAdImage() {
        return AdImage;
    }

    public void setAdImage(String adImage) {
        AdImage = adImage;
    }

    public String getAdStatus() {
        return AdStatus;
    }

    public void setAdStatus(String adStatus) {
        AdStatus = adStatus;
    }
}