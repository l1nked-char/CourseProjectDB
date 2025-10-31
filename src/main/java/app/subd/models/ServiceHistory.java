package app.subd.models;

public class ServiceHistory {
    private int id;
    private String historyId;
    private int serviceId;
    private int amount;
    private String serviceName;

    public ServiceHistory() {
        this.id = 0;
        this.historyId = "";
        this.serviceId = 0;
        this.amount = 0;
        this.serviceName = "";
    }

    public ServiceHistory(int id, String historyId, int serviceId, int amount) {
        this.id = id;
        this.historyId = historyId;
        this.serviceId = serviceId;
        this.amount = amount;
        this.serviceName = "";
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

    @Override
    public String toString() {return serviceName; }
}
