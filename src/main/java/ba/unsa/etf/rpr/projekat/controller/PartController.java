package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.dto.Part;
import ba.unsa.etf.rpr.projekat.dao.VehiclesDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class PartController {
    private VehiclesDAO dao;
    private Part part;

    public TextField partBrandField;
    public TextField partModelField;
    public TextField partNameField;
    public TextField partQuantityField;

    public PartController() {
        dao = null;
        part = null;
    }

    public PartController(VehiclesDAO dao, Part part) {
        this.dao = dao;
        this.part = part;
    }

    @FXML
    public void initialize() {
        if (part != null) {
            partBrandField.setText(part.getBrand());
            partModelField.setText(part.getModel());
            partNameField.setText(part.getName());
            partQuantityField.setText(Integer.toString(part.getQuantity()));
        }
    }

    public void okClick (ActionEvent actionEvent){
        int quantity = Integer.parseInt(partQuantityField.getText());
        if (part == null) {
            part = new Part(0, partBrandField.getText(), partModelField.getText(), partNameField.getText(), quantity);
            dao.addPart(part);
        } else {
            part.setBrand(partBrandField.getText());
            part.setModel(partModelField.getText());
            part.setName(partNameField.getText());
            part.setQuantity(Integer.parseInt(partQuantityField.getText()));
            dao.changePart(part);
        }
    }

}
