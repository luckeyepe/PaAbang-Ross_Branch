package capstone.abang.com.Models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Pc-user on 21/01/2018.
 */

public class CDFile {
    private String CDCode;
    private String CDModel;
    private long cdcaryear;
    private String CDMaker;
    private String cdchassisno;
    private String CDPhoto;
    private int CDCapacity;
    private String CDTransmission;
    private String cdplateno;
    private String cdengineno;
    private String cdcatcode;
    private String cdowner;
    private String cdstatus;
    private long cdrentrate;
    private String cdorcrimg;
    private String cdgarageimg;
    private int cdlikes;
    private String cdadditionalimgone;
    private String cdadditionalimgtwo;
    private String cdpostedon;
    private Map<String, Like> likes = new HashMap<>();
    private Map<String, CarPhotos> photos = new HashMap<>();
    private Map<String, ReservationFile> reservations = new HashMap<>();
    private String cdapplicationstatus;
    private String cdnotified;
    private int cdtransactions;

    public CDFile() {

    }

    public CDFile(String CDCode, String CDModel, long cdcaryear, String CDMaker, String cdchassisno, String CDPhoto, int CDCapacity, String CDTransmission, String cdplateno, String cdengineno, String cdcatcode, String cdowner, String cdstatus, long cdrentrate, String cdorcrimg, String cdgarageimg, int cdlikes, String cdadditionalimgone, String cdadditionalimgtwo, String cdpostedon, Map<String, Like> likes, Map<String, CarPhotos> photos, Map<String, ReservationFile> reservations, String cdapplicationstatus, String cdnotified, int cdtransactions) {
        this.CDCode = CDCode;
        this.CDModel = CDModel;
        this.cdcaryear = cdcaryear;
        this.CDMaker = CDMaker;
        this.cdchassisno = cdchassisno;
        this.CDPhoto = CDPhoto;
        this.CDCapacity = CDCapacity;
        this.CDTransmission = CDTransmission;
        this.cdplateno = cdplateno;
        this.cdengineno = cdengineno;
        this.cdcatcode = cdcatcode;
        this.cdowner = cdowner;
        this.cdstatus = cdstatus;
        this.cdrentrate = cdrentrate;
        this.cdorcrimg = cdorcrimg;
        this.cdgarageimg = cdgarageimg;
        this.cdlikes = cdlikes;
        this.cdadditionalimgone = cdadditionalimgone;
        this.cdadditionalimgtwo = cdadditionalimgtwo;
        this.cdpostedon = cdpostedon;
        this.likes = likes;
        this.photos = photos;
        this.reservations = reservations;
        this.cdapplicationstatus = cdapplicationstatus;
        this.cdnotified = cdnotified;
        this.cdtransactions = cdtransactions;
    }

    public int getCdtransactions() {
        return cdtransactions;
    }

    public void setCdtransactions(int cdtransactions) {
        this.cdtransactions = cdtransactions;
    }

    public String getCDCode() {
        return CDCode;
    }

    public void setCDCode(String CDCode) {
        this.CDCode = CDCode;
    }

    public String getCDModel() {
        return CDModel;
    }

    public void setCDModel(String CDModel) {
        this.CDModel = CDModel;
    }

    public long getCdcaryear() {
        return cdcaryear;
    }

    public void setCdcaryear(long cdcaryear) {
        this.cdcaryear = cdcaryear;
    }

    public String getCDMaker() {
        return CDMaker;
    }

    public void setCDMaker(String CDMaker) {
        this.CDMaker = CDMaker;
    }

    public String getCdchassisno() {
        return cdchassisno;
    }

    public void setCdchassisno(String cdchassisno) {
        this.cdchassisno = cdchassisno;
    }

    public String getCDPhoto() {
        return CDPhoto;
    }

    public void setCDPhoto(String CDPhoto) {
        this.CDPhoto = CDPhoto;
    }

    public int getCDCapacity() {
        return CDCapacity;
    }

    public void setCDCapacity(int CDCapacity) {
        this.CDCapacity = CDCapacity;
    }

    public String getCDTransmission() {
        return CDTransmission;
    }

    public void setCDTransmission(String CDTransmission) {
        this.CDTransmission = CDTransmission;
    }

    public String getCdplateno() {
        return cdplateno;
    }

    public void setCdplateno(String cdplateno) {
        this.cdplateno = cdplateno;
    }

    public String getCdengineno() {
        return cdengineno;
    }

    public void setCdengineno(String cdengineno) {
        this.cdengineno = cdengineno;
    }

    public String getCdcatcode() {
        return cdcatcode;
    }

    public void setCdcatcode(String cdcatcode) {
        this.cdcatcode = cdcatcode;
    }

    public String getCdowner() {
        return cdowner;
    }

    public void setCdowner(String cdowner) {
        this.cdowner = cdowner;
    }

    public String getCdstatus() {
        return cdstatus;
    }

    public void setCdstatus(String cdstatus) {
        this.cdstatus = cdstatus;
    }

    public long getCdrentrate() {
        return cdrentrate;
    }

    public void setCdrentrate(long cdrentrate) {
        this.cdrentrate = cdrentrate;
    }

    public String getCdorcrimg() {
        return cdorcrimg;
    }

    public void setCdorcrimg(String cdorcrimg) {
        this.cdorcrimg = cdorcrimg;
    }

    public String getCdgarageimg() {
        return cdgarageimg;
    }

    public void setCdgarageimg(String cdgarageimg) {
        this.cdgarageimg = cdgarageimg;
    }

    public int getCdlikes() {
        return cdlikes;
    }

    public void setCdlikes(int cdlikes) {
        this.cdlikes = cdlikes;
    }

    public String getCdadditionalimgone() {
        return cdadditionalimgone;
    }

    public void setCdadditionalimgone(String cdadditionalimgone) {
        this.cdadditionalimgone = cdadditionalimgone;
    }

    public String getCdadditionalimgtwo() {
        return cdadditionalimgtwo;
    }

    public void setCdadditionalimgtwo(String cdadditionalimgtwo) {
        this.cdadditionalimgtwo = cdadditionalimgtwo;
    }

    public String getCdpostedon() {
        return cdpostedon;
    }

    public void setCdpostedon(String cdpostedon) {
        this.cdpostedon = cdpostedon;
    }

    public Map<String, Like> getLikes() {
        return likes;
    }

    public void setLikes(Map<String, Like> likes) {
        this.likes = likes;
    }

    public Map<String, CarPhotos> getPhotos() {
        return photos;
    }

    public void setPhotos(Map<String, CarPhotos> photos) {
        this.photos = photos;
    }

    public Map<String, ReservationFile> getReservations() {
        return reservations;
    }

    public void setReservations(Map<String, ReservationFile> reservations) {
        this.reservations = reservations;
    }

    public String getCdapplicationstatus() {
        return cdapplicationstatus;
    }

    public void setCdapplicationstatus(String cdapplicationstatus) {
        this.cdapplicationstatus = cdapplicationstatus;
    }

    public String getCdnotified() {
        return cdnotified;
    }

    public void setCdnotified(String cdnotified) {
        this.cdnotified = cdnotified;
    }
}
