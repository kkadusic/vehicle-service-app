package ba.unsa.etf.rpr.projekat;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class Controller {

    public TableView<Owner> tableOwners;
    public TableColumn colOwnerId;
    public TableColumn<Owner, String> colFirstLastName;
    public TableColumn colNationalIdNumber;

    public TableView<Vehicle> tableVehicles;
    public TableColumn colVehicleId;
    public TableColumn<Vehicle, String> colBrand;
    public TableColumn colModel;
    public TableColumn colVehicleIdNumber;
    public TableColumn colNumberPlate;
    public TableColumn colModelYear;
    public TableColumn colEmissionStandard;
    public TableColumn colHorsepower;
    public TableColumn colEngine;

    public TableView<Part> tableParts;
    public TableColumn colPartId;
    public TableColumn colPartBrand;
    public TableColumn colPartModel;
    public TableColumn colPartName;
    public TableColumn colPartQuantity;

    private VehiclesDAO dao = null;

    @FXML
    public void initialize() {
        initializeDatabase();
    }

    public void initializeDatabase() {
        if (dao != null) dao.close();
        dao = new VehiclesDAOBase();
        initializeCommon();
    }

    public void initializeXml() {
        if (dao != null) dao.close();
        dao = new VehiclesDAOXML();
        initializeCommon();
    }

    public void initializeCommon() {
        tableOwners.setItems(dao.getOwners());
        colOwnerId.setCellValueFactory(new PropertyValueFactory("id"));
        colFirstLastName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFirstName().concat(" ").concat(data.getValue().getLastName())));
        colNationalIdNumber.setCellValueFactory(new PropertyValueFactory("nationalIdNumber"));

        tableVehicles.setItems(dao.getVehicles());
        colVehicleId.setCellValueFactory(new PropertyValueFactory("id"));
        colBrand.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBrand().getName()));
        colModel.setCellValueFactory(new PropertyValueFactory("model"));
        colVehicleIdNumber.setCellValueFactory(new PropertyValueFactory("vehicleIdNumber"));
        colNumberPlate.setCellValueFactory(new PropertyValueFactory("numberPlate"));
        colModelYear.setCellValueFactory(new PropertyValueFactory("modelYear"));
        colEmissionStandard.setCellValueFactory(new PropertyValueFactory("emissionStandard"));
        colHorsepower.setCellValueFactory(new PropertyValueFactory("horsepower"));
        colEngine.setCellValueFactory(new PropertyValueFactory("engine"));

        tableParts.setItems(dao.getParts());
        colPartId.setCellValueFactory(new PropertyValueFactory("id"));
        colPartBrand.setCellValueFactory(new PropertyValueFactory("brand"));
        colPartModel.setCellValueFactory(new PropertyValueFactory("model"));
        colPartName.setCellValueFactory(new PropertyValueFactory("name"));
        colPartQuantity.setCellValueFactory(new PropertyValueFactory("quantity"));
    }

    public void addOwnerAction(ActionEvent actionEvent) {
        Stage stage = new Stage();
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/owner.fxml"));
            OwnerController ownerController = new OwnerController(dao, null);
            loader.setController(ownerController);
            root = loader.load();
            stage.setTitle("Owner");
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.setResizable(false);
            stage.show();
            stage.setOnHiding(event -> tableOwners.setItems(dao.getOwners()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeOwnerAction(ActionEvent actionEvent) {
        Owner owner = tableOwners.getSelectionModel().getSelectedItem();
        if (owner == null) return;

        String firstLastName = owner.getFirstName().concat(" ").concat(owner.getLastName());

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation of removing an owner");
        alert.setHeaderText("Removing owner " + firstLastName);
        alert.setContentText("Are you sure that you want to remove owner " + firstLastName + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                dao.deleteOwner(owner);
            } catch (IllegalArgumentException e) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error by removing");
                alert.setHeaderText("It is now possible to remove owner " + firstLastName);
                alert.setContentText(e.getMessage());
                alert.show();
            }
            tableOwners.setItems(dao.getOwners());
        }
    }

    public void editOwnerAction(ActionEvent actionEvent) {
        Owner owner = tableOwners.getSelectionModel().getSelectedItem();
        if (owner == null) return;

        Stage stage = new Stage();
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/owner.fxml"));
            OwnerController ownerController = new OwnerController(dao, owner);
            loader.setController(ownerController);
            root = loader.load();
            stage.setTitle("Owner");
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.setResizable(false);
            stage.show();

            stage.setOnHiding(event -> tableOwners.setItems(dao.getOwners()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void addVehicleAction(ActionEvent actionEvent) {
        Stage stage = new Stage();
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/vehicle.fxml"));
            VehicleController vehicleController = new VehicleController(dao, null);
            loader.setController(vehicleController);
            root = loader.load();
            stage.setTitle("Vehicle");
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.setResizable(false);
            stage.show();

            stage.setOnHiding(event -> tableVehicles.setItems(dao.getVehicles()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeVehicleAction(ActionEvent actionEvent) {
        Vehicle vehicle = tableVehicles.getSelectionModel().getSelectedItem();
        if (vehicle == null) return;

        String brandModel = vehicle.getBrand().getName().concat(" ").concat(vehicle.getModel());

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation of removing vehicle");
        alert.setHeaderText("Removing vehicle " + brandModel);
        alert.setContentText("Are you sure that you want to remove vehicle " + brandModel + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            dao.deleteVehicle(vehicle);
            tableVehicles.setItems(dao.getVehicles());
        }
    }

    public void editVehicleAction(ActionEvent actionEvent) {
        Vehicle vehicle = tableVehicles.getSelectionModel().getSelectedItem();
        if (vehicle == null) return;

        Stage stage = new Stage();
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/vehicle.fxml"));
            VehicleController vehicleController = new VehicleController(dao, vehicle);
            loader.setController(vehicleController);
            root = loader.load();
            stage.setTitle("Vehicle");
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.setResizable(false);
            stage.show();

            stage.setOnHiding(event -> tableVehicles.setItems(dao.getVehicles()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addPartAction(ActionEvent actionEvent) {
        Stage stage = new Stage();
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/part.fxml"));
            PartController partController = new PartController(dao, null);
            loader.setController(partController);
            root = loader.load();
            stage.setTitle("Part");
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.setResizable(false);
            stage.show();

            stage.setOnHiding(event -> tableParts.setItems(dao.getParts()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removePartAction(ActionEvent actionEvent) {
        Part part = tableParts.getSelectionModel().getSelectedItem();
        if (part == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation of removing parts");
        alert.setHeaderText("Removing part: " + part.getName() + "\nQuantity: " + part.getQuantity());
        alert.setContentText("Are you sure that you want to remove parts " + part.getName() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            dao.deletePart(part);
            tableParts.setItems(dao.getParts());
        }
    }

    public void editPartAction(ActionEvent actionEvent) {
        Part part = tableParts.getSelectionModel().getSelectedItem();
        if (part == null) return;

        Stage stage = new Stage();
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/part.fxml"));
            PartController partController = new PartController(dao, part);
            loader.setController(partController);
            root = loader.load();
            stage.setTitle("Part");
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.setResizable(false);
            stage.show();

            stage.setOnHiding(event -> tableParts.setItems(dao.getParts()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchDb(ActionEvent actionEvent) {
        initializeDatabase();
    }

    public void switchXml(ActionEvent actionEvent) {
        initializeXml();
    }

    public void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Author: Kerim Kadušić" + "\nETF Sarajevo, RPR");
        alert.setContentText("Application for technical vehicle service, 2019.");
        alert.showAndWait();
    }

}
