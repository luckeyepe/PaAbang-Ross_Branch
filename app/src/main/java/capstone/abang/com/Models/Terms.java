package capstone.abang.com.Models;

/**
 * Created by sharls on 3/6/2018.
 */

public class Terms {
    private String tosAddedBy;
    private long tosDateAdded;
    private String tosFilePath;
    private String tosStatus;

    public Terms() {

    }

    public Terms(String tosAddedBy, long tosDateAdded, String tosFilePath, String tosStatus) {
        this.tosAddedBy = tosAddedBy;
        this.tosDateAdded = tosDateAdded;
        this.tosFilePath = tosFilePath;
        this.tosStatus = tosStatus;
    }

    public String getTosAddedBy() {
        return tosAddedBy;
    }

    public void setTosAddedBy(String tosAddedBy) {
        this.tosAddedBy = tosAddedBy;
    }

    public long getTosDateAdded() {
        return tosDateAdded;
    }

    public void setTosDateAdded(long tosDateAdded) {
        this.tosDateAdded = tosDateAdded;
    }

    public String getTosFilePath() {
        return tosFilePath;
    }

    public void setTosFilePath(String tosFilePath) {
        this.tosFilePath = tosFilePath;
    }

    public String getTosStatus() {
        return tosStatus;
    }

    public void setTosStatus(String tosStatus) {
        this.tosStatus = tosStatus;
    }
}
