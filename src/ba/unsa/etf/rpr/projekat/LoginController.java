package ba.unsa.etf.rpr.projekat;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class LoginController {

    public Button btnLogin;
    public PasswordField passwordField;
    public TextField usernameField;

    Controller mainController;

    public void openMainWindow(ActionEvent actionEvent) {
        boolean loginSuccessful = validateLogin(usernameField, passwordField);
        if (loginSuccessful) {
            Parent root = null;
            try {
                Stage myStage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/screen.fxml"));
                loader.load();
                mainController = loader.getController();
                myStage.setTitle("Main window");
                myStage.setScene(new Scene(loader.getRoot(), USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
                //myStage.getIcons().add(new Image("/img/car-icon.png"));
                myStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid data");
            alert.setHeaderText("The username or password you’ve \nentered doesn’t match any account");
            alert.setContentText("Please enter new username and password");
            Optional<ButtonType> result = alert.showAndWait();
        }
    }

    private boolean validateLogin(TextField username, TextField password) {
        if (username.getText().trim().isEmpty() || password.getText().trim().isEmpty()) {
            username.getStyleClass().removeAll("fieldRight");
            username.getStyleClass().add("fieldWrong");
            password.getStyleClass().removeAll("fieldRight");
            password.getStyleClass().add("fieldWrong");
            return false;
        } else {
            username.getStyleClass().removeAll("fieldWrong");
            username.getStyleClass().add("fieldRight");
            password.getStyleClass().removeAll("fieldWrong");
            password.getStyleClass().add("fieldRight");
        }
        return true;
    }

}
