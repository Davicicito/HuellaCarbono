import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase principal de lanzamiento de la aplicación EcoTrack.
 * Extiende de {@link Application} para inicializar el entorno gráfico de JavaFX,
 * cargar la vista inicial de acceso (Login) y configurar las propiedades del
 * escenario principal, como el estilo global y el dimensionamiento de la ventana.
 */
public class App extends Application {

    /**
     * Punto de entrada principal para la interfaz de usuario.
     * Coordina la carga del archivo FXML inicial, vincula la hoja de estilos CSS
     * y define el estado visual del escenario (Stage) antes de mostrarlo al usuario.
     * * @param stage El escenario principal proporcionado por la plataforma JavaFX.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

            stage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/ecotrack.png")));

            stage.setTitle("EcoTrack - Huella de Carbono");
            stage.setScene(scene);

            stage.setMaximized(true);

            // stage.setFullScreen(true);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}