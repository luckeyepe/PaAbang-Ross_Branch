package capstone.abang.com.Models;

/**
 * Created by Pc-user on 30/01/2018.
 */

public class CarSettings {
    private UDFile udFile;
    private UHFile uhFile;
    private CDFile cdFile;

    public CarSettings(UDFile udFile, UHFile uhFile, CDFile cdFile) {
        this.udFile = udFile;
        this.uhFile = uhFile;
        this.cdFile = cdFile;
    }

    public UDFile getUdFile() {
        return udFile;
    }

    public void setUdFile(UDFile udFile) {
        this.udFile = udFile;
    }

    public UHFile getUhFile() {
        return uhFile;
    }

    public void setUhFile(UHFile uhFile) {
        this.uhFile = uhFile;
    }

    public CDFile getCdFile() {
        return cdFile;
    }

    public void setCdFile(CDFile cdFile) {
        this.cdFile = cdFile;
    }
}
