package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.Main;
import ba.unsa.etf.rpr.projekat.dao.VehiclesDAO;
import ba.unsa.etf.rpr.projekat.dao.VehiclesDAOBase;
import ba.unsa.etf.rpr.projekat.dao.VehiclesDAOXML;
import ba.unsa.etf.rpr.projekat.dto.Owner;
import ba.unsa.etf.rpr.projekat.dto.Part;
import ba.unsa.etf.rpr.projekat.dto.Service;
import ba.unsa.etf.rpr.projekat.dto.Vehicle;
import ba.unsa.etf.rpr.projekat.report.PrintReport;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    public TableView<Service> tableServices;
    public TableColumn colServiceId;
    public TableColumn colServiceVehicleIdNumber;
    public TableColumn colMechanicName;
    public TableColumn colInspectionsNumber;
    public TableColumn colDetails;

    private ResourceBundle bundle;
    public ChoiceBox<String> jezikBox;
    private VehiclesDAO dao = null;
    public Label currentDateLabel;
    public ImageView compImage;

    private void reloadScene() {
        bundle = ResourceBundle.getBundle("Translation");
        Scene scene = tableOwners.getScene();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/screen.fxml"), bundle);
        loader.setController(this);
        try {
            scene.setRoot(loader.load());
        } catch (IOException ignored) {

        }
    }


    @FXML
    public void initialize() {
        initializeDatabase();

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd-MMM-yyyy");
        currentDateLabel.setText(formatter.format(date));
        compImage.setImage(new Image("/img/comp-logo.png"));


        // Detecting double click for owner editing
        tableOwners.setRowFactory(tv -> {
            TableRow<Owner> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    editOwnerAction(null);
                }
            });
            return row;
        });

        // Detecting double click for vehicle editing
        tableVehicles.setRowFactory(tv -> {
            TableRow<Vehicle> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    editVehicleAction(null);
                }
            });
            return row;
        });

        // Detecting double click for part editing
        tableParts.setRowFactory(tv -> {
            TableRow<Part> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    editPartAction(null);
                }
            });
            return row;
        });

        // Detecting double click for service editing
        tableServices.setRowFactory(tv -> {
            TableRow<Service> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    editServiceAction(null);
                }
            });
            return row;
        });
    }


    public void changeToBosnian(ActionEvent actionEvent){
        Locale.setDefault(new Locale("bs", "BA"));
        reloadScene();
    }

    public void changeToEnglish(ActionEvent actionEvent){
        Locale.setDefault(new Locale("en", "US"));
        reloadScene();
    }

    public void changeToGerman(ActionEvent actionEvent){
        Locale.setDefault(new Locale("de", "DE"));
        reloadScene();
    }

    public void changeToFrench(ActionEvent actionEvent){
        Locale.setDefault(new Locale("fr", "FR"));
        reloadScene();
    }


    public void setDarkMode() {
        bundle = ResourceBundle.getBundle("Translation");
        Scene scene = tableOwners.getScene();
        scene.getStylesheets().clear();
        scene.getStylesheets().add("/css/dark_mode.css");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/screen.fxml"), bundle);
        loader.setController(this);
        try {
            scene.setRoot(loader.load());
        } catch (IOException ignored) {

        }
    }

    public void setLightMode() {
        bundle = ResourceBundle.getBundle("Translation");
        Scene scene = tableOwners.getScene();
        scene.getStylesheets().clear();
        scene.getStylesheets().add("/css/style.css");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/screen.fxml"), bundle);
        loader.setController(this);
        try {
            scene.setRoot(loader.load());
        } catch (IOException ignored) {

        }
    }


    public void saveOwnersAction(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter1 = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
        FileChooser.ExtensionFilter extFilter2 = new FileChooser.ExtensionFilter("DOCX files (*.docx)", "*.docx");
        FileChooser.ExtensionFilter extFilter3 = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");

        fileChooser.getExtensionFilters().add(extFilter1);
        fileChooser.getExtensionFilters().add(extFilter2);
        fileChooser.getExtensionFilters().add(extFilter3);

        File file = fileChooser.showSaveDialog(Main.getStage());
        if (file != null) {
            PrintReport printReport = new PrintReport();
            try {
                printReport.saveOwnersAs(FilenameUtils.getExtension(file.getCanonicalPath()).toUpperCase(), VehiclesDAOBase.getConn(), file.getCanonicalPath());
            } catch (JRException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void saveVehiclesAction(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter1 = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
        FileChooser.ExtensionFilter extFilter2 = new FileChooser.ExtensionFilter("DOCX files (*.docx)", "*.docx");
        FileChooser.ExtensionFilter extFilter3 = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");

        fileChooser.getExtensionFilters().add(extFilter1);
        fileChooser.getExtensionFilters().add(extFilter2);
        fileChooser.getExtensionFilters().add(extFilter3);

        File file = fileChooser.showSaveDialog(Main.getStage());
        if (file != null) {
            PrintReport printReport = new PrintReport();
            try {
                printReport.saveVehiclesAs(FilenameUtils.getExtension(file.getCanonicalPath()).toUpperCase(), VehiclesDAOBase.getConn(), file.getCanonicalPath());
            } catch (JRException ex) {
                ex.printStackTrace();
            }
        }
    }




    public void printOwners(ActionEvent actionEvent) {
        try {
            new PrintReport().showOwnersReport(VehiclesDAOBase.getConn());
        } catch (JRException e1) {
            e1.printStackTrace();
        }
    }

    public void printVehicles(ActionEvent actionEvent) {
        try {
            new PrintReport().showVehiclesReport(VehiclesDAOBase.getConn());
        } catch (JRException e1) {
            e1.printStackTrace();
        }
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

        tableServices.setItems(dao.getServices());
        colServiceId.setCellValueFactory(new PropertyValueFactory("id"));
        colServiceVehicleIdNumber.setCellValueFactory(new PropertyValueFactory("vehicleIdNumber"));
        colMechanicName.setCellValueFactory(new PropertyValueFactory("mechanicName"));
        colInspectionsNumber.setCellValueFactory(new PropertyValueFactory("inspectionsNumber"));
        colDetails.setCellValueFactory(new PropertyValueFactory("details"));
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
            //stage.getIcons().add(new Image("/img/car-form.png"));
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
            //stage.getIcons().add(new Image("/img/car-icon.png"));
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

    public void addServiceAction(ActionEvent actionEvent) {
        Stage stage = new Stage();
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/service.fxml"));
            ServiceController serviceController = new ServiceController(dao, null);
            loader.setController(serviceController);
            root = loader.load();
            stage.setTitle("Add new service");
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.setResizable(false);
            stage.show();

            stage.setOnHiding(event -> tableServices.setItems(dao.getServices()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeServiceAction(ActionEvent actionEvent) {
        Service service = tableServices.getSelectionModel().getSelectedItem();
        if (service == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation of removing service");
        alert.setHeaderText("Removing service: " + service.getDetails());
        alert.setContentText("Are you sure that you want to remove services?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            dao.deleteService(service);
            tableServices.setItems(dao.getServices());
        }
    }

    public void editServiceAction(ActionEvent actionEvent) {
        Service service = tableServices.getSelectionModel().getSelectedItem();
        if (service == null) return;

        Stage stage = new Stage();
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/service.fxml"));
            ServiceController serviceController = new ServiceController(dao, service);
            loader.setController(serviceController);
            root = loader.load();
            stage.setTitle("Edit service");
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.setResizable(false);
            stage.show();

            stage.setOnHiding(event -> tableServices.setItems(dao.getServices()));
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

    public void exitClick(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }

}
