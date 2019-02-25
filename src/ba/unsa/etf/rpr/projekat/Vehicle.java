package ba.unsa.etf.rpr.projekat;

public class Vehicle {
    private int id;
    private Brand brand;
    private String model, vehicleIdNumber, numberPlate;
    private Owner owner;
    private int modelYear;
    private String emissionStandard;
    private String horsepower;
    private String engine;

    public Vehicle() {
    }

    public Vehicle(int id, Brand brand, String model, String vehicleIdNumber, String numberPlate, Owner owner,
                   int modelYear, String emissionStandard, String horsepower, String engine) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.vehicleIdNumber = vehicleIdNumber;
        this.numberPlate = numberPlate;
        this.owner = owner;
        this.modelYear = modelYear;
        this.emissionStandard = emissionStandard;
        this.horsepower = horsepower;
        this.engine = engine;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVehicleIdNumber() {
        return vehicleIdNumber;
    }

    public void setVehicleIdNumber(String vehicleIdNumber) {
        this.vehicleIdNumber = vehicleIdNumber;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    public void setNumberPlate(String numberPlate) {
        this.numberPlate = numberPlate;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public int getModelYear() {
        return modelYear;
    }

    public void setModelYear(int modelYear) {
        this.modelYear = modelYear;
    }

    public String getEmissionStandard() {
        return emissionStandard;
    }

    public void setEmissionStandard(String emissionStandard) {
        this.emissionStandard = emissionStandard;
    }

    public String getHorsepower() {
        return horsepower;
    }

    public void setHorsepower(String horsepower) {
        this.horsepower = horsepower;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }
}