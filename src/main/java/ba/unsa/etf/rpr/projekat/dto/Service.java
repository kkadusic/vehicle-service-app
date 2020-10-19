package ba.unsa.etf.rpr.projekat.dto;

public class Service {
    private int id;
    private String vehicleIdNumber;
    private String mechanicName;
    private int inspectionsNumber;
    private String details;

    public Service(int id, String vehicleIdNumber, String mechanicName, int inspectionsNumber, String details) {
        this.id = id;
        this.vehicleIdNumber = vehicleIdNumber;
        this.mechanicName = mechanicName;
        this.inspectionsNumber = inspectionsNumber;
        this.details = details;
    }

    public Service() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVehicleIdNumber() {
        return vehicleIdNumber;
    }

    public void setVehicleIdNumber(String vehicleIdNumber) {
        this.vehicleIdNumber = vehicleIdNumber;
    }

    public String getMechanicName() {
        return mechanicName;
    }

    public void setMechanicName(String mechanicName) {
        this.mechanicName = mechanicName;
    }

    public int getInspectionsNumber() {
        return inspectionsNumber;
    }

    public void setInspectionsNumber(int inspectionsNumber) {
        this.inspectionsNumber = inspectionsNumber;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
