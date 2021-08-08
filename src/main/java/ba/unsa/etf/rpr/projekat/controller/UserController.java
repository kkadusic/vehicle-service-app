package ba.unsa.etf.rpr.projekat.controller;

import ba.unsa.etf.rpr.projekat.dto.User;
import ba.unsa.etf.rpr.projekat.dao.VehiclesDAO;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UserController {
    private User user;
    private VehiclesDAO dao;

    public TextField usernameFieldForm;
    public PasswordField passwordFieldForm;
    public PasswordField passwordRepeatFieldForm;
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
        if (!usernameValidation(usernameFieldForm)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid username");
            alert.setHeaderText("Please enter new username");
            alert.setContentText("• Username should contain only alphabet letters\n" +
                    "numbers (0-9), period or underscore characters\n" +
                    "- For example: John5");
            alert.showAndWait();
        } else if (!passwordValidation(passwordFieldForm, passwordRepeatFieldForm)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid password");
            alert.setHeaderText("Please enter new password");
            alert.setContentText("• Password should contain more than 8 characters\n" +
                    "• Password should contain at least one upper case and one lower case alphabet\n" +
                    "• Password should contain at least one number\n" +
                    "- For example: Berlin123");
            alert.showAndWait();
        } else {
            user = new User(0, usernameFieldForm.getText(), passwordFieldForm.getText());
            dao.addUser(user);
            dao.close(); //So the database won't be locked (because of two connections)

            Stage stage = (Stage) btnOk.getScene().getWindow();
            stage.close();
        }
    }

    public void cancelClickUser(ActionEvent actionEvent) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private boolean passwordValidation(PasswordField passwordField, PasswordField passwordRepeatField) {
        String password = passwordField.getText();
        String passwordRepeat = passwordRepeatField.getText();

        if (password.length() != passwordRepeat.length()) return false;

        for (int i = 0; i < password.length(); i++) {
            if (password.charAt(i) != passwordRepeat.charAt(i)) return false;
        }

        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";
        return password.matches(passwordPattern);
    }

    private boolean usernameValidation(TextField usernameField) {
        String username = usernameField.getText();
        String usernamePattern = "[A-Za-z0-9_]+";
        return username.matches(usernamePattern);
    }
}
