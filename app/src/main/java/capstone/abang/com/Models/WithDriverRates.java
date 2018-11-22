package capstone.abang.com.Models;

/**
 * Created by Pc-user on 22/02/2018.
 */

public class WithDriverRates {
    private String categoryCode;
    private String distance;
    private String dropoffPrice;
    private String endPoint;
    private String roundtripPrice;
    private String startingPoint;
    private String price;

    public WithDriverRates() {

    }

    public WithDriverRates(String categoryCode, String distance, String dropoffPrice, String endPoint, String roundtripPrice, String startingPoint) {
        this.categoryCode = categoryCode;
        this.distance = distance;
        this.dropoffPrice = dropoffPrice;
        this.endPoint = endPoint;
        this.roundtripPrice = roundtripPrice;
        this.startingPoint = startingPoint;
    }

    public WithDriverRates(String categoryCode, String distance, String endPoint, String startingPoint, String price) {
        this.categoryCode = categoryCode;
        this.distance = distance;
        this.endPoint = endPoint;
        this.startingPoint = startingPoint;
        this.price = price;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDropoffPrice() {
        return dropoffPrice;
    }

    public void setDropoffPrice(String dropoffPrice) {
        this.dropoffPrice = dropoffPrice;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getRoundtripPrice() {
        return roundtripPrice;
    }

    public void setRoundtripPrice(String roundtripPrice) {
        this.roundtripPrice = roundtripPrice;
    }

    public String getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(String startingPoint) {
        this.startingPoint = startingPoint;
    }
}
