package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Huella;
import model.Habito;
import model.Usuario;
import services.HuellaService;
import services.HabitoService;
import utils.Sesion;
import java.util.List;
import java.util.Map;

public class InicioViewController {

    @FXML private Label lblHuellaTotal;
    @FXML private Label lblNumActividades;
    @FXML private Label lblPromedio;
    @FXML private Label lblVariacionHuella;
    @FXML private Label lblHabitosActivos; // Nuevo: Conectado al FXML
    @FXML private VBox vboxActividades;

    @FXML private Label lblCat1Valor;
    @FXML private ProgressBar pgCat1;

    private final HuellaService huellaService = new HuellaService();
    private final HabitoService habitoService = new HabitoService(); // Nuevo Service

    @FXML
    public void initialize() {
        Usuario usuarioActual = Sesion.getInstancia().getUsuario();
        if (usuarioActual != null) {
            cargarEstadisticas(usuarioActual.getId().longValue());
            cargarListaActividades(usuarioActual.getId());
            cargarHabitosResumen(usuarioActual.getId()); // Nueva llamada
        }
    }

    private void cargarEstadisticas(long userId) {
        Map<String, Double> stats = huellaService.obtenerEstadisticas(userId);
        double total = stats.getOrDefault("total", 0.0);
        double conteo = stats.getOrDefault("conteo", 0.0);

        lblHuellaTotal.setText(String.format("%.1f kg", total));
        lblNumActividades.setText(String.valueOf((int)conteo));

        double promedio = (conteo > 0) ? total / 30 : 0.0;
        lblPromedio.setText(String.format("%.1f kg", promedio));

        if (lblCat1Valor != null) {
            lblCat1Valor.setText(String.format("%.1f kg CO₂", total));
            pgCat1.setProgress(Math.min(total / 500.0, 1.0));
        }
    }

    private void cargarHabitosResumen(int userId) {
        // Obtenemos la lista de hábitos y ponemos el tamaño en el label
        List<Habito> habitos = habitoService.obtenerHabitosPorUsuario(userId);
        if (lblHabitosActivos != null) {
            lblHabitosActivos.setText(String.valueOf(habitos.size()));
        }
    }

    private void cargarListaActividades(int userId) {
        List<Huella> historial = huellaService.obtenerHistorial(userId);
        vboxActividades.getChildren().clear();
        int max = Math.min(historial.size(), 4);
        for (int i = 0; i < max; i++) {
            vboxActividades.getChildren().add(crearFilaActividad(historial.get(i)));
        }
    }

    private HBox crearFilaActividad(Huella h) {
        HBox row = new HBox(15);
        row.getStyleClass().add("activity-item");
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        String nombreAct = (h.getIdActividad() != null) ? h.getIdActividad().getNombre() : "Actividad";
        Label icon = new Label(asignarIcono(nombreAct));

        VBox texts = new VBox(2,
                new Label(nombreAct),
                new Label(h.getFecha().toString())
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(icon, texts, spacer, new Label(h.getValor() + " " + h.getUnidad()));
        return row;
    }

    private String asignarIcono(String nombre) {
        nombre = nombre.toLowerCase();
        if (nombre.contains("km") || nombre.contains("coche")) return "🚗";
        if (nombre.contains("kwh") || nombre.contains("luz")) return "⚡";
        return "🌱";
    }

    @FXML private void handleLogout() { cambiarEscena("/view/login.fxml"); }
    @FXML private void irAMisHuellas() { cambiarEscena("/view/mis_huellas.fxml"); }
    @FXML private void irAHabitos() { cambiarEscena("/view/habitos.fxml"); }
    @FXML private void irAAnalisis() { cambiarEscena("/view/analisis.fxml"); }
    @FXML private void irARecomendaciones() { cambiarEscena("/view/recomendaciones.fxml"); }

    private void cambiarEscena(String fxml) {
        try {
            Stage stage = (Stage) lblHuellaTotal.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) { e.printStackTrace(); }
    }
}