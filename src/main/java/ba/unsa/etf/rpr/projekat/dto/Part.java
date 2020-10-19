package ba.unsa.etf.rpr.projekat.dto;

public class Part {
    private int id;
    private String brand;
    private String model;
    private String name;
    private int quantity;

    public Part() {
    }

    public Part(int id, String brand, String model, String name, int quantity) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.name = name;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return name;
    }
}
