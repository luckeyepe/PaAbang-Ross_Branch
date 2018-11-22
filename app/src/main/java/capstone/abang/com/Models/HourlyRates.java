package capstone.abang.com.Models;

/**
 * Created by sharls on 3/4/2018.
 */

public class HourlyRates {
    private String categoryCode;
    private String packageEightHrs;
    private String packageExcessHr;
    private String packageTenHrs;
    private String packageThreeHrs;
    private String packageTwelveHrs;
    private String serviceCode;
    public HourlyRates() {

    }

    public HourlyRates(String categoryCode, String packageEightHrs, String packageExcessHr, String packageTenHrs, String packageThreeHrs, String packageTwelveHrs, String serviceCode) {
        this.categoryCode = categoryCode;
        this.packageEightHrs = packageEightHrs;
        this.packageExcessHr = packageExcessHr;
        this.packageTenHrs = packageTenHrs;
        this.packageThreeHrs = packageThreeHrs;
        this.packageTwelveHrs = packageTwelveHrs;
        this.serviceCode = serviceCode;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getPackageEightHrs() {
        return packageEightHrs;
    }

    public void setPackageEightHrs(String packageEightHrs) {
        this.packageEightHrs = packageEightHrs;
    }

    public String getPackageExcessHr() {
        return packageExcessHr;
    }

    public void setPackageExcessHr(String packageExcessHr) {
        this.packageExcessHr = packageExcessHr;
    }

    public String getPackageTenHrs() {
        return packageTenHrs;
    }

    public void setPackageTenHrs(String packageTenHrs) {
        this.packageTenHrs = packageTenHrs;
    }

    public String getPackageThreeHrs() {
        return packageThreeHrs;
    }

    public void setPackageThreeHrs(String packageThreeHrs) {
        this.packageThreeHrs = packageThreeHrs;
    }

    public String getPackageTwelveHrs() {
        return packageTwelveHrs;
    }

    public void setPackageTwelveHrs(String packageTwelveHrs) {
        this.packageTwelveHrs = packageTwelveHrs;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }
}
