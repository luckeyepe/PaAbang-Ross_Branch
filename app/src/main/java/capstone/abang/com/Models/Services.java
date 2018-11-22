package capstone.abang.com.Models;

/**
 * Created by Pc-user on 23/02/2018.
 */

public class Services {
    private String serviceName;
    private String serviceStatus;
    private String serviceType;

    public Services() {

    }

    public Services(String serviceName, String serviceStatus, String serviceType) {
        this.serviceName = serviceName;
        this.serviceStatus = serviceStatus;
        this.serviceType = serviceType;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }
}
