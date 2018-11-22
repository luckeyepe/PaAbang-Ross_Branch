package capstone.abang.com.Models;

/**
 * Created by Rylai on 2/20/2018.
 */

public class Location {
    private String code;
    private String lat;
    private String lng;

    private Location() {

    }

    public Location(String code, String lat, String lng) {
        this.code = code;
        this.lat = lat;
        this.lng = lng;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
