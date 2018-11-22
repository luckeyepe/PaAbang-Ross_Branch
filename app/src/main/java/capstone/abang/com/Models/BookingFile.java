package capstone.abang.com.Models;

/**
 * Created by Pc-user on 15/02/2018.
 */

public class BookingFile {
    private String BCode;
    private String BDateEnd;
    private String BDateStart;
    private String BDeliveryMode;
    private String BDestination;
    private String BOwnerCode;
    private String BRenterCode;
    private String BServiceType;
    private String BServiceMode;
    private String BStatus;
    private double BTotal;
    private String BCarCode;
    private String BFrom;
    private String BSchedDate;
    private String BSchedTime;
    private String BNotified;
    private String BOwnerStatus;
    private String BRenterStatus;
    private String BPrice;
    private String BExcessHour;
    private String BPackage;

    public BookingFile() {

    }

    public BookingFile(String BCode, String BDateEnd, String BDateStart, String BDeliveryMode, String BDestination, String BOwnerCode, String BRenterCode, String BServiceType, String BServiceMode, String BStatus, double BTotal, String BCarCode, String BFrom, String BSchedDate, String BSchedTime) {
        this.BCode = BCode;
        this.BDateEnd = BDateEnd;
        this.BDateStart = BDateStart;
        this.BDeliveryMode = BDeliveryMode;
        this.BDestination = BDestination;
        this.BOwnerCode = BOwnerCode;
        this.BRenterCode = BRenterCode;
        this.BServiceType = BServiceType;
        this.BServiceMode = BServiceMode;
        this.BStatus = BStatus;
        this.BTotal = BTotal;
        this.BCarCode = BCarCode;
        this.BFrom = BFrom;
        this.BSchedDate = BSchedDate;
        this.BSchedTime = BSchedTime;
    }
    //Seff Drive
    public BookingFile(String BCode, String BDateEnd, String BDateStart, String BDeliveryMode, String BOwnerCode, String BRenterCode, String BServiceType, String BStatus, double BTotal, String BCarCode, String BNotified, String BOwnerStatus, String BRenterStatus) {
        this.BCode = BCode;
        this.BDateEnd = BDateEnd;
        this.BDateStart = BDateStart;
        this.BDeliveryMode = BDeliveryMode;
        this.BOwnerCode = BOwnerCode;
        this.BRenterCode = BRenterCode;
        this.BServiceType = BServiceType;
        this.BStatus = BStatus;
        this.BTotal = BTotal;
        this.BCarCode = BCarCode;
        this.BNotified = BNotified;
        this.BOwnerStatus = BOwnerStatus;
        this.BRenterStatus = BRenterStatus;
    }
    //With driver / out of town
    public BookingFile(String BCode, String BDestination, String BOwnerCode, String BRenterCode, String BServiceType, String BServiceMode, String BStatus, double BTotal, String BCarCode, String BFrom, String BSchedDate, String BSchedTime, String BNotified, String BOwnerStatus, String BRenterStatus) {
        this.BCode = BCode;
        this.BDestination = BDestination;
        this.BOwnerCode = BOwnerCode;
        this.BRenterCode = BRenterCode;
        this.BServiceType = BServiceType;
        this.BServiceMode = BServiceMode;
        this.BStatus = BStatus;
        this.BTotal = BTotal;
        this.BCarCode = BCarCode;
        this.BFrom = BFrom;
        this.BSchedDate = BSchedDate;
        this.BSchedTime = BSchedTime;
        this.BNotified = BNotified;
        this.BOwnerStatus = BOwnerStatus;
        this.BRenterStatus = BRenterStatus;
    }

    //Hourly
    public BookingFile(String BCode, String BOwnerCode, String BRenterCode, String BStatus, String BCarCode, String BSchedDate, String BSchedTime, String BNotified, String BOwnerStatus, String BRenterStatus, String BPrice, String BExcessHour, String BPackage, String BServiceType) {
        this.BCode = BCode;
        this.BOwnerCode = BOwnerCode;
        this.BRenterCode = BRenterCode;
        this.BStatus = BStatus;
        this.BCarCode = BCarCode;
        this.BSchedDate = BSchedDate;
        this.BSchedTime = BSchedTime;
        this.BNotified = BNotified;
        this.BOwnerStatus = BOwnerStatus;
        this.BRenterStatus = BRenterStatus;
        this.BPrice = BPrice;
        this.BExcessHour = BExcessHour;
        this.BPackage = BPackage;
        this.BServiceType = BServiceType;
    }

    public String getBCode() {
        return BCode;
    }

    public void setBCode(String BCode) {
        this.BCode = BCode;
    }

    public String getBDateEnd() {
        return BDateEnd;
    }

    public void setBDateEnd(String BDateEnd) {
        this.BDateEnd = BDateEnd;
    }

    public String getBDateStart() {
        return BDateStart;
    }

    public void setBDateStart(String BDateStart) {
        this.BDateStart = BDateStart;
    }

    public String getBDeliveryMode() {
        return BDeliveryMode;
    }

    public void setBDeliveryMode(String BDeliveryMode) {
        this.BDeliveryMode = BDeliveryMode;
    }

    public String getBDestination() {
        return BDestination;
    }

    public void setBDestination(String BDestination) {
        this.BDestination = BDestination;
    }

    public String getBOwnerCode() {
        return BOwnerCode;
    }

    public void setBOwnerCode(String BOwnerCode) {
        this.BOwnerCode = BOwnerCode;
    }

    public String getBRenterCode() {
        return BRenterCode;
    }

    public void setBRenterCode(String BRenterCode) {
        this.BRenterCode = BRenterCode;
    }

    public String getBServiceType() {
        return BServiceType;
    }

    public void setBServiceType(String BServiceType) {
        this.BServiceType = BServiceType;
    }

    public String getBServiceMode() {
        return BServiceMode;
    }

    public void setBServiceMode(String BServiceMode) {
        this.BServiceMode = BServiceMode;
    }

    public String getBStatus() {
        return BStatus;
    }

    public void setBStatus(String BStatus) {
        this.BStatus = BStatus;
    }

    public double getBTotal() {
        return BTotal;
    }

    public void setBTotal(double BTotal) {
        this.BTotal = BTotal;
    }

    public String getBCarCode() {
        return BCarCode;
    }

    public void setBCarCode(String BCarCode) {
        this.BCarCode = BCarCode;
    }

    public String getBFrom() {
        return BFrom;
    }

    public void setBFrom(String BFrom) {
        this.BFrom = BFrom;
    }

    public String getBSchedDate() {
        return BSchedDate;
    }

    public void setBSchedDate(String BSchedDate) {
        this.BSchedDate = BSchedDate;
    }

    public String getBSchedTime() {
        return BSchedTime;
    }

    public void setBSchedTime(String BSchedTime) {
        this.BSchedTime = BSchedTime;
    }

    public String getBNotified() {
        return BNotified;
    }

    public void setBNotified(String BNotified) {
        this.BNotified = BNotified;
    }

    public String getBOwnerStatus() {
        return BOwnerStatus;
    }

    public void setBOwnerStatus(String BOwnerStatus) {
        this.BOwnerStatus = BOwnerStatus;
    }

    public String getBRenterStatus() {
        return BRenterStatus;
    }

    public void setBRenterStatus(String BRenterStatus) {
        this.BRenterStatus = BRenterStatus;
    }

    public String getBPrice() {
        return BPrice;
    }

    public void setBPrice(String BPrice) {
        this.BPrice = BPrice;
    }

    public String getBExcessHour() {
        return BExcessHour;
    }

    public void setBExcessHour(String BExcessHour) {
        this.BExcessHour = BExcessHour;
    }
}
