package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Habito;
import services.HabitoService;
import utils.Sesion;
import java.util.List;

public class HabitosViewController {

    @FXML private FlowPane flowHabitos;
    @FXML private HBox hboxSugerencias;
    @FXML private Label lblTotalHabitos;

    private final HabitoService habitoService = new HabitoService();

    @FXML
    public void initialize() {
        cargarHabitosUsuario();
        cargarSugerenciasManuales();
    }

    private void cargarHabitosUsuario() {
        if (Sesion.getInstancia().getUsuario() == null) return;
        int userId = Sesion.getInstancia().getUsuario().getId();

        List<Habito> habitos = habitoService.misHabitos(userId);

        Platform.runLater(() -> {
            // Verificación de seguridad para evitar el NullPointerException
            if (lblTotalHabitos != null) {
                lblTotalHabitos.setText(String.valueOf(habitos.size()));
            }

            if (flowHabitos != null) {
                flowHabitos.getChildren().clear();
                for (Habito h : habitos) {
                    flowHabitos.getChildren().add(crearTarjetaPro(h));
                }
            }
        });
    }

    private void cargarSugerenciasManuales() {
        if (hboxSugerencias == null) return;
        hboxSugerencias.getChildren().clear();
        hboxSugerencias.setSpacing(20);
        hboxSugerencias.getChildren().addAll(
                crearTarjetaSugerencia("Usar bicicleta", "-30 kg CO₂/mes", "🚲", "bg-purple"),
                crearTarjetaSugerencia("Dieta vegetariana", "-45 kg CO₂/mes", "🍴", "bg-rose"),
                crearTarjetaSugerencia("Reciclar residuos", "-20 kg CO₂/mes", "♻️", "bg-blue")
        );
    }

    private VBox crearTarjetaSugerencia(String titulo, String ahorro, String icono, String clase) {
        VBox card = new VBox(15);
        card.getStyleClass().add("recom-card");
        card.setPrefWidth(320);
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        StackPane iconPane = new StackPane(new Label(icono));
        iconPane.getStyleClass().add(clase);

        VBox textContent = new VBox(2, new Label(titulo), new Label(ahorro));
        header.getChildren().addAll(iconPane, textContent);

        Button btn = new Button("Agregar hábito");
        btn.getStyleClass().add("btn-adoptar");
        btn.setMaxWidth(Double.MAX_VALUE);

        card.getChildren().addAll(header, btn);
        return card;
    }

    private VBox crearTarjetaPro(Habito h) {
        VBox card = new VBox(15);
        card.getStyleClass().add("stat-card");
        card.setPrefWidth(350);
        Label title = new Label(h.getIdActividad().getNombre());
        title.setStyle("-fx-font-weight: bold;");
        card.getChildren().addAll(title, new Label(h.getFrecuencia() + "x/" + h.getTipo()));
        return card;
    }

    @FXML private void irAInicio() { cambiarEscena("/view/inicio.fxml"); }
    @FXML private void irAMisHuellas() { cambiarEscena("/view/mis_huellas.fxml"); }
    @FXML private void handleLogout() {
        Sesion.getInstancia().setUsuario(null);
        cambiarEscena("/view/login.fxml");
    }

    private void cambiarEscena(String fxml) {
        try {
            Stage stage = (Stage) flowHabitos.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) { e.printStackTrace(); }
    }
}