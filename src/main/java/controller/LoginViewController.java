package controller;

import model.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginViewController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;

    private final UsuarioController usuarioController = new UsuarioController();

    @FXML
    void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String pass = passwordField.getText();

        Usuario u = usuarioController.login(email, pass);

        if (u != null) {
            utils.Sesion.getInstancia().setUsuario(u);

            System.out.println("Login exitoso: " + u.getNombre());
            abrirVista("/view/inicio.fxml");
        } else {
            mostrarAlerta("Error", "❌ Credenciales incorrectas");
        }
    }

    @FXML
    void showRegister() {
        abrirVista("/view/registro.fxml");
    }

    private void abrirVista(String ruta) {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            Scene scene = new Scene(loader.load());
            // Importante: Volvemos a cargar el CSS para que la nueva vista se vea bien
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}