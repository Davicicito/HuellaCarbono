package controller;

import model.Usuario;
import services.UsuarioService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Controlador para la pantalla de inicio de sesión (Login).
 * Se encarga de validar la identidad del usuario contra la base de datos
 * y establecer el contexto de la sesión global antes de permitir el acceso
 * al resto de funcionalidades de la aplicación.
 */
public class LoginViewController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;

    /**
     * Instancia del servicio de usuarios para gestionar la lógica de autenticación.
     */
    private final UsuarioService usuarioService = new UsuarioService();

    /**
     * Gestiona el proceso de autenticación cuando el usuario pulsa el botón de entrar.
     * Recoge los datos del formulario, limpia espacios innecesarios y solicita
     * la validación al servicio. Si tiene éxito, guarda el usuario en la sesión global.
     * @param event Evento de acción del botón.
     */
    @FXML
    void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String pass = passwordField.getText().trim();

        // Verificación básica de que no existan campos vacíos antes de procesar
        if (email.isEmpty() || pass.isEmpty()) {
            mostrarAlerta("Campos vacíos", "⚠️ Por favor, rellena todos los campos.");
            return;
        }

        // Delegación de la validación al servicio de negocio
        Usuario u = usuarioService.login(email, pass);

        if (u != null) {
            // Establecemos el usuario logueado en el Singleton de sesión para uso global
            utils.Sesion.getInstancia().setUsuario(u);
            System.out.println("Login exitoso: " + u.getNombre());
            abrirVista("/view/inicio.fxml");
        } else {
            mostrarAlerta("Error", "❌ Credenciales incorrectas");
        }
    }

    /**
     * Redirige al usuario hacia la pantalla de registro si aún no tiene una cuenta.
     */
    @FXML
    void showRegister() {
        abrirVista("/view/registro.fxml");
    }

    /**
     * Realiza el cambio de escena en la ventana actual.
     * Carga el archivo FXML correspondiente y aplica los estilos visuales definidos.
     * @param ruta Ruta relativa del archivo .fxml dentro del proyecto.
     */
    private void abrirVista(String ruta) {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            Scene scene = new Scene(loader.load());
            // Aplicación de la hoja de estilos para mantener la coherencia visual
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error al cargar la vista: " + ruta);
            e.printStackTrace();
        }
    }

    /**
     * Muestra un cuadro de diálogo de error personalizado.
     * Utilizado para informar sobre fallos en la autenticación o campos incompletos.
     * @param titulo Encabezado de la alerta.
     * @param mensaje Cuerpo descriptivo del error.
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}