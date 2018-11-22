package capstone.abang.com.Models;



public class ReservationFile {
    private String ResCode;
    private String ResDateStart;
    private String ResDateEnd;
    private String ResDestination;
    private String ResServiceType;
    private String ResServiceMode;
    private String ResDeliveryMode;
    private float ResTotal;
    private String ResOwnerCode;
    private String ResRenterCode;
    private String ResStatus;
    private String ResCarCode;
    private String ResFrom;
    private String ResDateSchedule;
    private String ResTimeSchedule;
    private String ResNotify;
    private String ResNotifyTime;
    private String ResNotifyRenter;
    private String ResNotifyRenterTime;
    private String ResPrice;
    private String ResPackage;
    private String ResExcessRate;
    private String ResNotifType;


    public ReservationFile() {


    }

    public ReservationFile(String resCode, String resDateStart, String resDateEnd, String resDestination, String resServiceType, String resServiceMode, String resDeliveryMode, float resTotal, String resOwnerCode, String resRenterCode, String resStatus, String resCarCode, String resFrom, String resDateSchedule, String resTimeSchedule, String resNotify, String resNotifyTime, String resNotifyRenter, String resNotifyRenterTime, String resNotifType) {
        ResCode = resCode;
        ResDateStart = resDateStart;
        ResDateEnd = resDateEnd;
        ResDestination = resDestination;
        ResServiceType = resServiceType;
        ResServiceMode = resServiceMode;
        ResDeliveryMode = resDeliveryMode;
        ResTotal = resTotal;
        ResOwnerCode = resOwnerCode;
        ResRenterCode = resRenterCode;
        ResStatus = resStatus;
        ResCarCode = resCarCode;
        ResFrom = resFrom;
        ResDateSchedule = resDateSchedule;
        ResTimeSchedule = resTimeSchedule;
        ResNotify = resNotify;
        ResNotifyTime = resNotifyTime;
        ResNotifyRenter = resNotifyRenter;
        ResNotifyRenterTime = resNotifyRenterTime;
        ResNotifType = resNotifType;
    }
    //Hourly
    public ReservationFile(String resCode, String resOwnerCode, String resRenterCode, String resStatus, String resCarCode, String resDateSchedule, String resTimeSchedule, String resNotify, String resNotifyTime, String resNotifyRenter, String resNotifyRenterTime, String resPrice, String resPackage, String resExcessRate, String resServiceType, String resNotifType) {
        ResCode = resCode;
        ResOwnerCode = resOwnerCode;
        ResRenterCode = resRenterCode;
        ResStatus = resStatus;
        ResCarCode = resCarCode;
        ResDateSchedule = resDateSchedule;
        ResTimeSchedule = resTimeSchedule;
        ResNotify = resNotify;
        ResNotifyTime = resNotifyTime;
        ResNotifyRenter = resNotifyRenter;
        ResNotifyRenterTime = resNotifyRenterTime;
        ResPrice = resPrice;
        ResPackage = resPackage;
        ResExcessRate = resExcessRate;
        ResServiceType = resServiceType;
        ResNotifType = resNotifType;
    }
    // ENDORSEMENT
    public ReservationFile(String resCode, String resOwnerCode, String resStatus, String resCarCode, String resNotify, String resNotifyTime, String resNotifType) {
        ResCode = resCode;
        ResOwnerCode = resOwnerCode;
        ResStatus = resStatus;
        ResCarCode = resCarCode;
        ResNotify = resNotify;
        ResNotifyTime = resNotifyTime;
        ResNotifType = resNotifType;
    }

    public String getResPrice() {
        return ResPrice;
    }

    public void setResPrice(String resPrice) {
        ResPrice = resPrice;
    }

    public String getResPackage() {
        return ResPackage;
    }

    public void setResPackage(String resPackage) {
        ResPackage = resPackage;
    }

    public String getResExcessRate() {
        return ResExcessRate;
    }

    public void setResExcessRate(String resExcessRate) {
        ResExcessRate = resExcessRate;
    }

    public String getResCode() {
        return ResCode;
    }

    public void setResCode(String resCode) {
        ResCode = resCode;
    }

    public String getResDateStart() {
        return ResDateStart;
    }

    public void setResDateStart(String resDateStart) {
        ResDateStart = resDateStart;
    }

    public String getResDateEnd() {
        return ResDateEnd;
    }

    public void setResDateEnd(String resDateEnd) {
        ResDateEnd = resDateEnd;
    }

    public String getResDestination() {
        return ResDestination;
    }

    public void setResDestination(String resDestination) {
        ResDestination = resDestination;
    }

    public String getResServiceType() {
        return ResServiceType;
    }

    public void setResServiceType(String resServiceType) {
        ResServiceType = resServiceType;
    }

    public String getResServiceMode() {
        return ResServiceMode;
    }

    public void setResServiceMode(String resServiceMode) {
        ResServiceMode = resServiceMode;
    }

    public String getResDeliveryMode() {
        return ResDeliveryMode;
    }

    public void setResDeliveryMode(String resDeliveryMode) {
        ResDeliveryMode = resDeliveryMode;
    }

    public float getResTotal() {
        return ResTotal;
    }

    public void setResTotal(float resTotal) {
        ResTotal = resTotal;
    }

    public String getResOwnerCode() {
        return ResOwnerCode;
    }

    public void setResOwnerCode(String resOwnerCode) {
        ResOwnerCode = resOwnerCode;
    }

    public String getResRenterCode() {
        return ResRenterCode;
    }

    public void setResRenterCode(String resRenterCode) {
        ResRenterCode = resRenterCode;
    }

    public String getResStatus() {
        return ResStatus;
    }

    public void setResStatus(String resStatus) {
        ResStatus = resStatus;
    }

    public String getResCarCode() {
        return ResCarCode;
    }

    public void setResCarCode(String resCarCode) {
        ResCarCode = resCarCode;
    }

    public String getResFrom() {
        return ResFrom;
    }

    public void setResFrom(String resFrom) {
        ResFrom = resFrom;
    }

    public String getResDateSchedule() {
        return ResDateSchedule;
    }

    public void setResDateSchedule(String resDateSchedule) {
        ResDateSchedule = resDateSchedule;
    }

    public String getResTimeSchedule() {
        return ResTimeSchedule;
    }

    public void setResTimeSchedule(String resTimeSchedule) {
        ResTimeSchedule = resTimeSchedule;
    }

    public String getResNotify() {
        return ResNotify;
    }

    public void setResNotify(String resNotify) {
        ResNotify = resNotify;
    }

    public String getResNotifyTime() {
        return ResNotifyTime;
    }

    public void setResNotifyTime(String resNotifyTime) {
        ResNotifyTime = resNotifyTime;
    }

    public String getResNotifyRenter() {
        return ResNotifyRenter;
    }

    public void setResNotifyRenter(String resNotifyRenter) {
        ResNotifyRenter = resNotifyRenter;
    }

    public String getResNotifyRenterTime() {
        return ResNotifyRenterTime;
    }

    public void setResNotifyRenterTime(String resNotifyRenterTime) {
        ResNotifyRenterTime = resNotifyRenterTime;
    }

    public String getResNotifType() {
        return ResNotifType;
    }

    public void setResNotifType(String resNotifType) {
        ResNotifType = resNotifType;
    }




}
