package ba.unsa.etf.rpr.projekat;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class LoginController {

    public Button btnLogin;
    public PasswordField passwordField;
    public TextField usernameField;

    Controller mainController;

    private Connection conn = null;
    private PreparedStatement getUsersQuery, getNewUserIdQuery, addUserQuery;

    public void openMainWindow(ActionEvent actionEvent) {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:resources/db/vehicle.db");
            getUsersQuery = conn.prepareStatement("SELECT * FROM user WHERE username = ? AND password = ?");
            getNewUserIdQuery = conn.prepareStatement("SELECT MAX(id)+1 FROM user");
            addUserQuery = conn.prepareStatement("INSERT INTO user VALUES (?,?,?)");

            getUsersQuery.setString(1, usernameField.getText());
            getUsersQuery.setString(2, passwordField.getText());
            ResultSet rs = getUsersQuery.executeQuery();

            if (rs.next()) {
                Stage myStage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/screen.fxml"));
                loader.load();
                mainController = loader.getController();
                myStage.setTitle("Main screen");
                myStage.setScene(new Scene(loader.getRoot(), USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
                //myStage.getIcons().add(new Image("/img/car-icon.png"));
                myStage.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid data");
                alert.setHeaderText("The username or password you’ve \nentered doesn’t match any account");
                alert.setContentText("Please enter new username and password");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
