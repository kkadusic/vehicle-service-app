package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.dto.Location;
import ba.unsa.etf.rpr.projekat.dto.Owner;
import ba.unsa.etf.rpr.projekat.dao.VehiclesDAO;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

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

    public OwnerController() {
        dao = null;
        owner = null;
    }

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

    public void okClick(ActionEvent actionEvent) {
        // Validation
        boolean allOk = validateEmpty(firstNameField);
        allOk &= validateEmpty(lastNameField);
        allOk &= validateEmpty(parentNameField);
        allOk &= validateEmpty(addressField);

        if (dateOfBirthPicker.getValue() == null || dateOfBirthPicker.getValue().isAfter(LocalDate.now())) {
            dateOfBirthPicker.getStyleClass().removeAll("fieldRight");
            dateOfBirthPicker.getStyleClass().add("fieldWrong");
            allOk = false;
        } else {
            dateOfBirthPicker.getStyleClass().removeAll("fieldWrong");
            dateOfBirthPicker.getStyleClass().add("fieldRight");
        }

        // National ID number validation
        if (!validateNationalIdNumber(nationalIdNumberField)) {
            nationalIdNumberField.getStyleClass().removeAll("fieldRight");
            nationalIdNumberField.getStyleClass().add("fieldWrong");
            allOk = false;
        } else {
            nationalIdNumberField.getStyleClass().removeAll("fieldWrong");
            nationalIdNumberField.getStyleClass().add("fieldRight");
        }

        // Checking if location is new or already existing one
        Location placeOfBirth = validateLocation(placeOfBirthCombo, null);
        if (placeOfBirth == null) allOk = false;

        Location placeOfResidence = validateLocation(placeOfResidenceCombo, postalCodeField);
        if (placeOfResidence == null) allOk = false;

        if (!allOk) return;

        for (Location l : locations) {
            if (l.getName().equals(placeOfResidence.getName())) {
                addChangeOwner(placeOfBirth, placeOfResidence);
                return;
            }
        }
    }


    private void addChangeOwner(Location placeOfBirth, Location placeOfResidence) {
        if (owner == null) {
            owner = new Owner(0, firstNameField.getText(), lastNameField.getText(), parentNameField.getText(),
                    dateOfBirthPicker.getValue(), placeOfBirth, addressField.getText(), placeOfResidence, nationalIdNumberField.getText());
            dao.addOwner(owner);
        } else {
            owner.setFirstName(firstNameField.getText());
            owner.setLastName(lastNameField.getText());
            owner.setParentName(parentNameField.getText());
            owner.setDateOfBirth(dateOfBirthPicker.getValue());
            owner.setPlaceOfBirth(placeOfBirth);
            owner.setAddress(addressField.getText());
            owner.setPlaceOfResidence(placeOfResidence);
            owner.setNationalIdNumber(nationalIdNumberField.getText());
            dao.changeOwner(owner);
        }
    }

    private Location validateLocation(ComboBox locationCombo, TextField postalCodeField) {
        // ComboBox is empty (null)
        if (locationCombo.getValue() == null) {
            locationCombo.getStyleClass().removeAll("fieldRight");
            locationCombo.getStyleClass().add("fieldWrong");
            return null;
        }

        Location location = null;
        String locationName = locationCombo.getValue().toString().trim();
        for (Location l : locations) {
            if (l.getName().equals(locationName)) {
                location = l;
            }
        }
        if (location == null) {
            // Owner inserted a new location
            if (locationName.trim().isEmpty()) {
                locationCombo.getStyleClass().removeAll("fieldRight");
                locationCombo.getStyleClass().add("fieldWrong");
            } else {
                locationCombo.getStyleClass().removeAll("fieldWrong");
                locationCombo.getStyleClass().add("fieldRight");

                // Postal code validation
                String postalCode = "";
                if (postalCodeField != null) {
                    if (!validateEmpty(postalCodeField)) {
                        postalCodeField.getStyleClass().removeAll("fieldRight");
                        postalCodeField.getStyleClass().add("fieldWrong");
                        return location;
                    }

                    // Postal code must be number
                    try {
                        Integer.parseInt(postalCodeField.getText());
                    } catch (Exception e) {
                        postalCodeField.getStyleClass().removeAll("fieldRight");
                        postalCodeField.getStyleClass().add("fieldWrong");
                        return location;
                    }
                    postalCode = postalCodeField.getText();
                }
                location = new Location(0, locationName, postalCode);
            }

        } else {
            // Existing location is choosen
            locationCombo.getStyleClass().removeAll("fieldWrong");
            locationCombo.getStyleClass().add("fieldRight");
            // If user has choosen one of the existing places, postal code must be equal to the choosen one
            if (postalCodeField != null) {
                if (!validateEmpty(postalCodeField) || !postalCodeField.getText().equals(location.getPostalCode())) {
                    postalCodeField.getStyleClass().removeAll("fieldRight");
                    postalCodeField.getStyleClass().add("fieldWrong");
                    return null;
                }
            }
        }
        return location;
    }

    private boolean validateNationalIdNumber(TextField field) {
        String nationalIdNumber = field.getText();
        if (nationalIdNumber.length() > 9 || nationalIdNumber.length() < 1) return false;
        for (int i = 0; i < nationalIdNumber.length(); i++) {
            if (!Character.isDigit(nationalIdNumber.charAt(i)) && !Character.isAlphabetic(nationalIdNumber.charAt(i)))
                return false;
        }
        return true;
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

    public void cancelClick(ActionEvent actionEvent) {
        Stage stage = (Stage) placeOfBirthCombo.getScene().getWindow();
        stage.close();
    }

    public void locationChange(ActionEvent actionEvent) {
        String locationName = placeOfResidenceCombo.getValue().toString().trim();
        for (Location l : locations) {
            if (l.getName().equals(locationName)) {
                postalCodeField.setText(l.getPostalCode());
            }
        }
    }

}
