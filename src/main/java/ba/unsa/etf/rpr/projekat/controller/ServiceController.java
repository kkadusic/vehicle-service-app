package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.dto.Service;
import ba.unsa.etf.rpr.projekat.dao.VehiclesDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ServiceController {
    private VehiclesDAO dao;
    private Service service;

    public TextField serviceVehicleIdNumberField;
    public TextField serviceMechanicNameField;
    public TextField serviceInspectionsNumberField;
    public TextField serviceDetailsField;

    public ServiceController() {
        dao = null;
        service = null;
    }

    public ServiceController(VehiclesDAO dao, Service service) {
        this.dao = dao;
        this.service = service;
    }

    @FXML
    public void initialize() {
        if (service != null) {
            serviceVehicleIdNumberField.setText(service.getVehicleIdNumber());
            serviceMechanicNameField.setText(service.getMechanicName());
            serviceInspectionsNumberField.setText(Integer.toString(service.getInspectionsNumber()));
            serviceDetailsField.setText(service.getDetails());
        }
    }

    public void okClick(ActionEvent actionEvent) {
        int inspections = Integer.parseInt(serviceInspectionsNumberField.getText());
        if (service == null) {
            service = new Service(0, serviceVehicleIdNumberField.getText(), serviceMechanicNameField.getText(), inspections, serviceDetailsField.getText());
            dao.addService(service);
        } else {
            service.setVehicleIdNumber(serviceVehicleIdNumberField.getText());
            service.setMechanicName(serviceMechanicNameField.getText());
            service.setInspectionsNumber(Integer.parseInt(serviceInspectionsNumberField.getText()));
            service.setDetails(serviceDetailsField.getText());
            dao.changeService(service);
        }
    }
}
