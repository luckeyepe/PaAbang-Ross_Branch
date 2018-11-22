package capstone.abang.com.Models;

/**
 * Created by Pc-user on 16/02/2018.
 */

public class SelfDriveRates {
    private String autoDailyPrice;
    private String autoMonthlyPrice;
    private String autoWeeklyPrice;
    private String categoryCode;
    private String manualDailyPrice;
    private String manualWeeklyPrice;
    private String manualMonthlyPrice;

    public SelfDriveRates() {

    }

    public SelfDriveRates(String autoDailyPrice, String autoMonthlyPrice, String autoWeeklyPrice, String categoryCode, String manualDailyPrice, String manualWeeklyPrice, String manualMonthlyPrice) {
        this.autoDailyPrice = autoDailyPrice;
        this.autoMonthlyPrice = autoMonthlyPrice;
        this.autoWeeklyPrice = autoWeeklyPrice;
        this.categoryCode = categoryCode;
        this.manualDailyPrice = manualDailyPrice;
        this.manualWeeklyPrice = manualWeeklyPrice;
        this.manualMonthlyPrice = manualMonthlyPrice;
    }

    public String getAutoDailyPrice() {
        return autoDailyPrice;
    }

    public void setAutoDailyPrice(String autoDailyPrice) {
        this.autoDailyPrice = autoDailyPrice;
    }

    public String getAutoMonthlyPrice() {
        return autoMonthlyPrice;
    }

    public void setAutoMonthlyPrice(String autoMonthlyPrice) {
        this.autoMonthlyPrice = autoMonthlyPrice;
    }

    public String getAutoWeeklyPrice() {
        return autoWeeklyPrice;
    }

    public void setAutoWeeklyPrice(String autoWeeklyPrice) {
        this.autoWeeklyPrice = autoWeeklyPrice;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getManualDailyPrice() {
        return manualDailyPrice;
    }

    public void setManualDailyPrice(String manualDailyPrice) {
        this.manualDailyPrice = manualDailyPrice;
    }

    public String getManualWeeklyPrice() {
        return manualWeeklyPrice;
    }

    public void setManualWeeklyPrice(String manualWeeklyPrice) {
        this.manualWeeklyPrice = manualWeeklyPrice;
    }

    public String getManualMonthlyPrice() {
        return manualMonthlyPrice;
    }

    public void setManualMonthlyPrice(String manualMonthlyPrice) {
        this.manualMonthlyPrice = manualMonthlyPrice;
    }
}
