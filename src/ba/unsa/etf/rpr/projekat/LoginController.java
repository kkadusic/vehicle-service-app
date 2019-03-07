package ba.unsa.etf.rpr.projekat;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class LoginController {

    public TextField usernameField;
    public PasswordField passwordField;
    public Button btnLogin;
    public Button btnNewUser;

    Controller mainController;

    private Connection conn = null;
    private PreparedStatement getUsersQuery, getNewUserIdQuery, addUserQuery;
    private VehiclesDAO dao = null;

    @FXML
    public void initialize() {
        initializeDatabase();
    }

    public void initializeDatabase() {
        if (dao != null) dao.close();
        dao = new VehiclesDAOBase();
    }

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
                conn.close(); //So the database won't be locked (because of two connections)
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

    public void addUserAction(ActionEvent actionEvent) {
        Stage stage = new Stage();
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user.fxml"));
            UserController userController = new UserController(dao, null);
            loader.setController(userController);
            root = loader.load();
            stage.setTitle("User");
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
