package app.subd.models;

public class ServiceHistory {
    private int id;
    private String historyId;
    private int serviceId;
    private int amount;
    private String serviceName;
    private int serviceNameId;

    public ServiceHistory() {
        this.id = 0;
        this.historyId = "";
        this.serviceId = 0;
        this.amount = 0;
        this.serviceName = "";
        this.serviceNameId = 0;
    }

    public ServiceHistory(int id, String historyId, int serviceId, int amount,  String serviceName,  int serviceNameId) {
        this.id = id;
        this.historyId = historyId;
        this.serviceId = serviceId;
        this.amount = amount;
        this.serviceName = serviceName;
        this.serviceNameId = serviceNameId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getHistoryId() { return historyId; }
    public void setHistoryId(String historyId) { this.historyId = historyId; }
    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public int getServiceNameId() { return serviceNameId; }
    public void setServiceNameId(int serviceNameId) { this.serviceNameId = serviceNameId; }

    @Override
    public String toString() {return serviceName; }
}
