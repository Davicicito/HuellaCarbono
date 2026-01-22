package controller;

import model.Usuario;
import services.UsuarioService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;

/**
 * Controlador para la gestión del alta de nuevos usuarios (Registro).
 * Se encarga de capturar los datos del formulario, realizar validaciones de integridad
 * y solicitar al servicio de negocio la creación física de la cuenta en el sistema.
 */
public class RegistroViewController {

    @FXML private TextField nombreField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    /**
     * Servicio de lógica de negocio para la gestión de entidades de usuario.
     */
    private final UsuarioService usuarioService = new UsuarioService();

    /**
     * Procesa la solicitud de registro al pulsar el botón correspondiente.
     * Captura las entradas, verifica que no existan campos vacíos y construye
     * una instancia de {@link Usuario} para ser persistida. Si el proceso tiene éxito,
     * informa al usuario y le redirige a la pantalla de acceso.
     */
    @FXML
    private void handleRegistro() {
        String nombre = nombreField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        // Validación de campos obligatorios para asegurar la consistencia de los datos
        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Error", "⚠️ Rellena todos los campos");
            return;
        }

        // Creación y mapeo del nuevo objeto de dominio
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setContrasena(password);
        nuevoUsuario.setFechaRegistro(LocalDate.now());

        // Delegación de la lógica de registro al servicio, que gestiona posibles duplicados
        if (usuarioService.registrarUsuario(nuevoUsuario)) {
            mostrarAlertaExito();
            irALogin();
        } else {
            mostrarAlerta("Error", "❌ No se pudo registrar. El email ya existe.");
        }
    }

    /**
     * Redirige al usuario de vuelta a la pantalla de Login.
     * Útil para usuarios que ya poseen una cuenta o tras un registro exitoso.
     */
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

    /**
     * Informa al usuario de que su cuenta ha sido creada satisfactoriamente.
     */
    private void mostrarAlertaExito() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registro Completado");
        alert.setHeaderText(null);
        alert.setContentText("¡Cuenta creada con éxito!");
        alert.showAndWait();
    }

    /**
     * Presenta un cuadro de diálogo con detalles sobre un error ocurrido durante el proceso.
     * @param titulo Encabezado de la ventana de alerta.
     * @param mensaje Descripción detallada del fallo detectado.
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}