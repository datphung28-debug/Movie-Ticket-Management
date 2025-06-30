package movieticketbookingmanagement;

import java.io.IOException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.Parent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.stage.StageStyle;

public class FXMLDocumentController implements Initializable {
    
    @FXML
    private AnchorPane topform;

    @FXML
    private Button signIn_close, signIn_loginBtn, signIn_minimize;
    @FXML
    private Hyperlink signIn_createAccount;

    @FXML
    private AnchorPane signIn_form;
    @FXML
    private TextField signIn_username;
    @FXML
    private PasswordField signIn_password;

    @FXML
    private Button signUp_btn, signUp_close, signUp_minimize;
    @FXML
    private Hyperlink signUp_alreadyHaveAccount;
    @FXML
    private AnchorPane signUp_form;
    @FXML
    private TextField signUp_email, signUp_username;
    @FXML
    private PasswordField signUp_password;

    // JDBC variables
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    public boolean validEmail() {
        Pattern pattern = Pattern.compile("[a-zA-Z0-9][a-zA-Z0-9._]*@[a-zA-Z0-9]+([.][a-zA-Z]+)+");
        Matcher match = pattern.matcher(signUp_email.getText());

        Alert alert;

        if (match.find() && match.group().matches(signUp_email.getText())) {

            return true;
        } else {
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Thông báo lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Email không hợp lệ");
            alert.showAndWait();

            return false;
        }

    }

    @FXML
    public void signup() {

        String sql = "INSERT INTO admin (email, username, password) VALUES (?,?,?)";
        String sqll = "SELECT * FROM admin";
        connect = database.connectDb();

        try {
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, signUp_email.getText());
            prepare.setString(2, signUp_username.getText());
            prepare.setString(3, signUp_password.getText());

            Alert alert;

            if (signUp_email.getText().isEmpty() || signUp_username.getText().isEmpty() || signUp_password.getText().isEmpty()) {
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Thông báo lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Vui lòng điền đầy đủ thông tin");
                alert.showAndWait();

            } else if (signUp_password.getText().length() < 8) {
                alert = new Alert(AlertType.ERROR);
                alert.setTitle("Thông báo lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Mật khẩu phải đủ 8 ký tự trở lên");
                alert.showAndWait();
            } else {

                if (validEmail()) {

                    prepare = connect.prepareStatement(sqll);
                    result = prepare.executeQuery();

                    if (result.next()) {
                        alert = new Alert(AlertType.ERROR);
                        alert.setTitle("Thông báo lỗi");
                        alert.setHeaderText(null);
                        alert.setContentText(signUp_email.getText() + " đã tồn tại!");
                        alert.showAndWait();

                    } else {
                        prepare.execute();

                        alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Thông báo");
                        alert.setHeaderText(null);
                        alert.setContentText("Tạo tài khoản mới thành công!");
                        alert.showAndWait();

                        signUp_email.setText("");
                        signUp_username.setText("");
                        signUp_password.setText("");
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private double x = 0;
    private double y = 0;

    @FXML
    public void signin() throws IOException {
        String username = signIn_username.getText();
        String password = signIn_password.getText();

        Alert alert = new Alert(Alert.AlertType.NONE);

        if (username.isEmpty() || password.isEmpty()) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Thông báo lỗi");
            alert.setContentText("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
            alert.showAndWait();
            return;
        }

        String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";

        try {
            connect = database.connectDb();
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, username);
            prepare.setString(2, password);

            result = prepare.executeQuery();

            if (result.next()) {
                
                getData.username = signIn_username.getText();
                
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setTitle("Đăng nhập thành công");
                alert.setContentText("Chào mừng " + username + "!");
                alert.showAndWait();

                Parent root = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                
                root.setOnMousePressed((MouseEvent event) -> {
                    
                    x = event.getSceneX();
                    y = event.getSceneY();
                    
                });
                
                root.setOnMouseDragged((MouseEvent event) ->{
                    
                    stage.setX(event.getScreenX() - x);
                    stage.setY(event.getScreenY() - y);
                    
                });
                
                stage.initStyle(StageStyle.TRANSPARENT);
                
                stage.setScene(scene);
                stage.show();

                // Ẩn cửa sổ hiện tại
                signIn_form.getScene().getWindow().hide();

            } else {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setTitle("Thông báo lỗi");
                alert.setContentText("Tên đăng nhập hoặc mật khẩu không đúng.");
                alert.showAndWait();
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (result != null) {
                    result.close();
                }
                if (prepare != null) {
                    prepare.close();
                }
                if (connect != null) {
                    connect.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void switchForm(ActionEvent event) {
        if (event.getSource() == signIn_createAccount) {
            signIn_form.setVisible(false);
            signUp_form.setVisible(true);
        } else if (event.getSource() == signUp_alreadyHaveAccount) {
            signIn_form.setVisible(true);
            signUp_form.setVisible(false);
        }
    }

    @FXML
    public void signIn_close() {
        Platform.exit();
    }

    @FXML
    public void signUp_close() {
        Platform.exit();
    }

    @FXML
    public void signIn_minimize() {
        ((Stage) signIn_form.getScene().getWindow()).setIconified(true);
    }

    @FXML
    public void signUp_minimize() {
        ((Stage) signUp_form.getScene().getWindow()).setIconified(true);
    }

    public void minimize() {
        
        Stage stage = (Stage)topform.getScene().getWindow();
        stage.setIconified(true);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
}
