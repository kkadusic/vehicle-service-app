package ba.unsa.etf.rpr.projekat;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class OwnerController {
    private VehiclesDAO dao;
    private Owner owner;

    public TextField firstNameField;
    public TextField lastNameField;
    public TextField parentNameField;
    public TextField addressField;
    public TextField postalCodeField;
    public TextField nationalIdNumberField;
    public ComboBox placeOfBirthCombo;
    public ComboBox placeOfResidenceCombo;
    public DatePicker dateOfBirthPicker;

    private ObservableList<Location> locations;

    public OwnerController(VehiclesDAO dao, Owner owner) {
        this.dao = dao;
        this.owner = owner;
        locations = dao.getLocations();
    }

    @FXML
    public void initialize() {
        placeOfBirthCombo.setItems(locations);
        placeOfResidenceCombo.setItems(locations);

        if (owner != null) {
            firstNameField.setText(owner.getFirstName());
            lastNameField.setText(owner.getLastName());
            parentNameField.setText(owner.getParentName());
            dateOfBirthPicker.setValue(owner.getDateOfBirth());
            placeOfBirthCombo.setValue(owner.getPlaceOfBirth().getName());
            addressField.setText(owner.getAddress());
            placeOfResidenceCombo.setValue(owner.getPlaceOfResidence().getName());
            postalCodeField.setText(owner.getPlaceOfResidence().getPostalCode());
            nationalIdNumberField.setText(owner.getNationalIdNumber());
        }
    }

    public void handleOk (ActionEvent actionEvent){

    }
}
