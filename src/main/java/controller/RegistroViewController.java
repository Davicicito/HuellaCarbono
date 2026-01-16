package controller;

import model.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;

public class RegistroViewController {

    @FXML private TextField nombreField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    private final UsuarioController usuarioController = new UsuarioController();

    @FXML
    private void handleRegistro() {
        String nombre = nombreField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Error", "⚠️ Rellena todos los campos");
            return;
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setContrasena(password);
        nuevoUsuario.setFechaRegistro(LocalDate.now());

        if (usuarioController.registrar(nuevoUsuario)) {
            mostrarAlertaExito();
            irALogin();
        } else {
            mostrarAlerta("Error", "❌ No se pudo registrar. El email ya existe.");
        }
    }

    @FXML
    private void irALogin() {
        try {
            Stage stage = (Stage) nombreField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlertaExito() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registro Completado");
        alert.setHeaderText(null);
        alert.setContentText("¡Cuenta creada con éxito!");
        alert.showAndWait();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}