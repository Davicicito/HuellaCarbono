package controller;

import javafx.event.ActionEvent;
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
import model.Usuario;
import services.HuellaService;
import utils.Sesion;
import java.util.List;
import java.util.Map;

public class InicioViewController {

    @FXML private Label lblHuellaTotal;
    @FXML private Label lblNumActividades;
    @FXML private Label lblPromedio;
    @FXML private Label lblVariacionHuella;
    @FXML private VBox vboxActividades;

    // Elementos para las barras de progreso reales
    @FXML private Label lblCat1Nombre;
    @FXML private Label lblCat1Valor;
    @FXML private ProgressBar pgCat1;

    private final HuellaService huellaService = new HuellaService();

    @FXML
    public void initialize() {
        Usuario usuarioActual = Sesion.getInstancia().getUsuario();

        if (usuarioActual != null) {
            // 1. Cargar tarjetas estadísticas
            cargarEstadisticas(usuarioActual.getId().longValue());
            // 2. Cargar lista de actividades recientes
            cargarListaActividades(usuarioActual.getId());
        }
    }

    private void cargarEstadisticas(long userId) {
        Map<String, Double> stats = huellaService.obtenerEstadisticas(userId);

        double total = stats.get("total");
        double conteo = stats.get("conteo");

        // Seteamos los valores de la base de datos (esto pisa el texto del FXML)
        lblHuellaTotal.setText(String.format("%.1f kg", total));
        lblNumActividades.setText(String.valueOf((int)conteo));

        double promedio = (conteo > 0) ? total / 30 : 0.0;
        lblPromedio.setText(String.format("%.1f kg", promedio));

        // Actualizamos las barras de progreso con el total
        if (lblCat1Valor != null) {
            lblCat1Valor.setText(String.format("%.1f kg CO₂", total));
            pgCat1.setProgress(Math.min(total / 500.0, 1.0)); // Ejemplo: 500kg es el 100%
        }
    }

    private void cargarListaActividades(int userId) {
        List<Huella> historial = huellaService.obtenerHistorial(userId);
        vboxActividades.getChildren().clear();

        // Mostramos las últimas 4 actividades de la base de datos
        int max = Math.min(historial.size(), 4);
        for (int i = 0; i < max; i++) {
            vboxActividades.getChildren().add(crearFilaActividad(historial.get(i)));
        }
    }

    private HBox crearFilaActividad(Huella h) {
        HBox row = new HBox(15);
        row.getStyleClass().add("activity-item");
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // Icono dinámico según la actividad
        String nombreAct = (h.getIdActividad() != null) ? h.getIdActividad().getNombre() : "Actividad";
        Label icon = new Label(asignarIcono(nombreAct));

        VBox texts = new VBox(2);
        Label title = new Label(nombreAct);
        title.setStyle("-fx-font-weight: bold;");

        Label date = new Label(h.getFecha().toString());
        date.setStyle("-fx-font-size: 10px; -fx-text-fill: #71717a;");
        texts.getChildren().addAll(title, date);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Usamos los campos 'valor' y 'unidad' de tu tabla MySQL
        Label valor = new Label(h.getValor() + " " + h.getUnidad());
        valor.setStyle("-fx-font-weight: bold;");

        row.getChildren().addAll(icon, texts, spacer, valor);
        return row;
    }

    private String asignarIcono(String nombre) {
        nombre = nombre.toLowerCase();
        if (nombre.contains("km") || nombre.contains("coche")) return "🚗";
        if (nombre.contains("kwh") || nombre.contains("luz")) return "⚡";
        return "🌱";
    }

    @FXML
    private void handleLogout() {
        try {
            Stage stage = (Stage) lblHuellaTotal.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void irAMisHuellas() {
        try {
            // Obtenemos la ventana actual
            Stage stage = (Stage) lblHuellaTotal.getScene().getWindow();

            // Cargamos el FXML de Mis Huellas
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/mis_huellas.fxml"));
            Scene scene = new Scene(loader.load());

            // Aplicamos el CSS para que no pierda el diseño
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("EcoTrack - Mis Huellas");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}