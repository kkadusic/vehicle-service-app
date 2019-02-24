package ba.unsa.etf.rpr.projekat;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
            String lastFirstName = vehicle.getOwner().getFirstName().concat(" ").concat(vehicle.getOwner().getLastName());
            ownerCombo.setValue(lastFirstName);
        }
    }

    public void okClick(ActionEvent actionEvent) {
        // Validation
        boolean allOk = validateEmpty(modelField);
        allOk &= validateEmpty(vehicleIdNumberField);
        if (!validatePlateNumber(numberPlateField)) {
            numberPlateField.getStyleClass().removeAll("fieldRight");
            numberPlateField.getStyleClass().add("fieldWrong");
            allOk = false;
        } else {
            numberPlateField.getStyleClass().removeAll("fieldWrong");
            numberPlateField.getStyleClass().add("fieldRight");
        }

        Brand brand = validateBrand(brandCombo);
        if (brand == null) allOk = false;

        Owner owner = validateOwner(ownerCombo);
        if (owner == null) allOk = false;

        if (!allOk) return;

        // Adding new owner or editing existing one
        if (vehicle == null) {
            vehicle = new Vehicle(0, brand, modelField.getText(), vehicleIdNumberField.getText(), numberPlateField.getText(), owner);
            dao.addVehicle(vehicle);
        } else {
            vehicle.setBrand(brand);
            vehicle.setModel(modelField.getText());
            vehicle.setVehicleIdNumber(vehicleIdNumberField.getText());
            vehicle.setNumberPlate(numberPlateField.getText());
            vehicle.setOwner(owner);
            dao.changeVehicle(vehicle);
        }

        Stage stage = (Stage) brandCombo.getScene().getWindow();
        stage.close();
    }

    private boolean validateEmpty(TextField field) {
        if (field.getText().trim().isEmpty()) {
            field.getStyleClass().removeAll("fieldRight");
            field.getStyleClass().add("fieldWrong");
            return false;
        } else {
            field.getStyleClass().removeAll("fieldWrong");
            field.getStyleClass().add("fieldRight");
        }
        return true;
    }

    private boolean validatePlateNumber(TextField field) {
        String numberPlate = field.getText();
        if (numberPlate.length() > 9) return false;
        for (int i = 0; i < numberPlate.length(); i++) {
            if (!Character.isDigit(numberPlate.charAt(i)) || !Character.isAlphabetic(numberPlate.charAt(i)))
                return false;
        }
        return true;
    }

    private Brand validateBrand(ComboBox brandCombo) {
        // ComboBox is empty (null)
        if (brandCombo.getValue() == null) {
            brandCombo.getStyleClass().removeAll("fieldRight");
            brandCombo.getStyleClass().add("fieldWrong");
            return null;
        }

        Brand brand = null;
        String brandName = brandCombo.getValue().toString().trim();
        for (Brand b : brands) {
            if (b.getName().equals(brandName)) {
                brand = b;
            }
        }
        if (brand == null) {
            if (brandName.trim().isEmpty()) {
                brandCombo.getStyleClass().removeAll("fieldRight");
                brandCombo.getStyleClass().add("fieldWrong");
            } else {
                brandCombo.getStyleClass().removeAll("fieldWrong");
                brandCombo.getStyleClass().add("fieldRight");
                brand = new Brand(0, brandName);
            }
        } else {
            brandCombo.getStyleClass().removeAll("fieldWrong");
            brandCombo.getStyleClass().add("fieldRight");
        }
        return brand;
    }

    private Owner validateOwner(ComboBox ownerCombo) {
        // ComboBox is empty (null)
        if (ownerCombo.getValue() == null) {
            ownerCombo.getStyleClass().removeAll("fieldRight");
            ownerCombo.getStyleClass().add("fieldWrong");
            return null;
        }

        Owner owner = null;
        String ownerName = ownerCombo.getValue().toString().trim();
        for (Owner o : owners) {
            if (o.toString().equals(ownerName)) {
                owner = o;
            }
        }
        // Exists
        ownerCombo.getStyleClass().removeAll("fieldWrong");
        ownerCombo.getStyleClass().add("fieldRight");
        return owner;
    }

    public void cancelClick(ActionEvent actionEvent) {
        Stage stage = (Stage) brandCombo.getScene().getWindow();
        stage.close();
    }
}
