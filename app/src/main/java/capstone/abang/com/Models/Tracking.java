package capstone.abang.com.Models;


public class Tracking {
    private String code,lat,lng;



    public Tracking() {
        super();
    }
    public Tracking(String code, String lat, String lng) {
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
