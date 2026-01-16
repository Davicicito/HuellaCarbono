import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

            stage.setTitle("EcoTrack - Huella de Carbono");
            stage.setScene(scene);

            // --- CONFIGURACIÓN DE PANTALLA COMPLETA ---
            stage.setMaximized(true);
            // Si quieres que ni siquiera se vea la barra de arriba (Modo Kiosko):
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