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

/**
 * Controlador de la vista principal o "Dashboard".
 * Centraliza la información más relevante del usuario, como el impacto total de CO2,
 * el recuento de actividades recientes y el estado de sus hábitos activos, ofreciendo
 * una visión general e inmediata de su progreso ambiental.
 */
public class InicioViewController {

    @FXML private Label lblHuellaTotal;
    @FXML private Label lblNumActividades;
    @FXML private Label lblPromedio;
    @FXML private Label lblVariacionHuella;
    @FXML private Label lblHabitosActivos;
    @FXML private VBox vboxActividades;

    @FXML private Label lblCat1Valor;
    @FXML private ProgressBar pgCat1;

    private final HuellaService huellaService = new HuellaService();
    private final HabitoService habitoService = new HabitoService();

    /**
     * Inicializa el tablero principal tras la carga del FXML.
     * Recupera el usuario de la sesión y coordina la carga de estadísticas,
     * el historial reciente y el resumen de hábitos.
     */
    @FXML
    public void initialize() {
        Usuario usuarioActual = Sesion.getInstancia().getUsuario();
        if (usuarioActual != null) {
            cargarEstadisticas(usuarioActual.getId().longValue());
            cargarListaActividades(usuarioActual.getId());
            cargarHabitosResumen(usuarioActual.getId());
        }
    }

    /**
     * Solicita al servicio las métricas calculadas del usuario.
     * Actualiza los indicadores numéricos de la interfaz y la barra de progreso
     * que representa el impacto acumulado frente a un límite objetivo.
     * @param userId Identificador único del usuario para filtrar los datos.
     */
    private void cargarEstadisticas(long userId) {
        Map<String, Double> stats = huellaService.obtenerEstadisticas(userId);
        double total = stats.getOrDefault("total", 0.0);
        double conteo = stats.getOrDefault("conteo", 0.0);

        lblHuellaTotal.setText(String.format("%.1f kg", total));
        lblNumActividades.setText(String.valueOf((int)conteo));

        // Cálculo estimado del promedio diario (basado en un ciclo mensual de 30 días)
        double promedio = (conteo > 0) ? total / 30 : 0.0;
        lblPromedio.setText(String.format("%.1f kg", promedio));

        if (lblCat1Valor != null) {
            lblCat1Valor.setText(String.format("%.1f kg CO₂", total));
            pgCat1.setProgress(Math.min(total / 500.0, 1.0));
        }
    }

    /**
     * Recupera y contabiliza los hábitos comprometidos por el usuario.
     * Actualiza el contador visual de "Hábitos Activos" en el panel de resumen.
     * @param userId Identificador del usuario.
     */
    private void cargarHabitosResumen(int userId) {
        List<Habito> habitos = habitoService.obtenerHabitosPorUsuario(userId);
        if (lblHabitosActivos != null) {
            lblHabitosActivos.setText(String.valueOf(habitos.size()));
        }
    }

    /**
     * Carga el historial de las últimas acciones registradas.
     * Limita la visualización a las 4 actividades más recientes para mantener
     * la estética y limpieza del Dashboard.
     * @param userId Identificador del usuario.
     */
    private void cargarListaActividades(int userId) {
        List<Huella> historial = huellaService.obtenerHistorial(userId);
        vboxActividades.getChildren().clear();
        int max = Math.min(historial.size(), 4);
        for (int i = 0; i < max; i++) {
            vboxActividades.getChildren().add(crearFilaActividad(historial.get(i)));
        }
    }

    /**
     * Construye un componente visual de fila para representar una actividad individual.
     * Asigna iconos representativos y formatea los textos de fecha y valor.
     * @param h Objeto de tipo Huella con los datos de la actividad.
     * @return HBox configurado para ser insertado en la lista de actividades recientes.
     */
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

    /**
     * Selecciona un emoji adecuado según la naturaleza de la actividad.
     * Analiza el nombre de la actividad para identificar palabras clave sobre transporte,
     * energía o residuos.
     * @param nombre Texto descriptivo de la actividad.
     * @return Un icono en formato String.
     */
    private String asignarIcono(String nombre) {
        nombre = nombre.toLowerCase();
        if (nombre.contains("km") || nombre.contains("coche")) return "🚗";
        if (nombre.contains("kwh") || nombre.contains("luz")) return "⚡";
        return "🌱";
    }

    // --- MÉTODOS DE NAVEGACIÓN Y CIERRE DE SESIÓN ---

    @FXML private void handleLogout() { cambiarEscena("/view/login.fxml"); }
    @FXML private void irAMisHuellas() { cambiarEscena("/view/mis_huellas.fxml"); }
    @FXML private void irAHabitos() { cambiarEscena("/view/habitos.fxml"); }
    @FXML private void irAAnalisis() { cambiarEscena("/view/analisis.fxml"); }
    @FXML private void irARecomendaciones() { cambiarEscena("/view/recomendaciones.fxml"); }

    /**
     * Gestiona el reemplazo de la escena actual en la ventana principal.
     * @param fxml Ruta del recurso FXML a cargar.
     */
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