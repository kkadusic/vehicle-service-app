package ba.unsa.etf.rpr.projekat;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UserController {
    private User user;
    private VehiclesDAO dao;

    public TextField usernameFieldForm;
    public PasswordField passwordFieldForm;
    public Button btnOk;
    public Button btnCancel;

    public UserController() {
        dao = null;
        user = null;
    }

    public UserController(VehiclesDAO dao, User user) {
        this.dao = dao;
        this.user = user;
    }

    public void okClickUser(ActionEvent actionEvent) {
        user = new User(0, usernameFieldForm.getText(), passwordFieldForm.getText());
        dao.addUser(user);
        dao.close(); //So the database won't be locked (because of two connections)

        Stage stage = (Stage) btnOk.getScene().getWindow();
        stage.close();
    }

    public void cancelClickUser(ActionEvent actionEvent) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}
