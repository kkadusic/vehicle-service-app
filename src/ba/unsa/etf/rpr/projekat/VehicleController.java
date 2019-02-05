package ba.unsa.etf.rpr.projekat;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class VehicleController {
    private VehiclesDAO dao;
    private Vehicle vehicle;

    public ComboBox brandCombo;
    public ComboBox ownerCombo;
    public TextField modelField;
    public TextField vehicleIdNumberField;
    public TextField numberPlateField;

    private ObservableList<Owner> owners;
    private ObservableList<Brand> brands;

    public VehicleController() {
        dao = null;
        vehicle = null;
    }

    public VehicleController(VehiclesDAO dao, Vehicle vehicle) {
        this.dao = dao;
        this.vehicle = vehicle;
        owners = dao.getOwners();
        brands = dao.getBrands();
    }

    @FXML
    public void initialize() {
        ownerCombo.setItems(owners);
        brandCombo.setItems(brands);

        if (vehicle != null) {
            brandCombo.setValue(vehicle.getBrand().getName());
            modelField.setText(vehicle.getModel());
            vehicleIdNumberField.setText(vehicle.getVehicleIdNumber());
            numberPlateField.setText(vehicle.getNumberPlate());
            String lastFirstName = vehicle.getOwner().getLastName().concat(" ").concat(vehicle.getOwner().getFirstName());
            ownerCombo.setValue(lastFirstName);
        }
    }

}
