package capstone.abang.com.Models;

/**
 * Created by Pc-user on 14/01/2018.
 */

public class USettings {
    private UDFile udFile;
    private UHFile uhFile;

    public USettings(UDFile udFile, UHFile uhFile) {
        this.udFile = udFile;
        this.uhFile = uhFile;
    }

    public USettings() {

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
}
