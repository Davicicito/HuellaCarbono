package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Huella;
import services.HuellaService;
import services.HabitoService;
import utils.Sesion;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnalisisViewController {

    @FXML private LineChart<String, Number> chartEvolucion;
    @FXML private PieChart chartCategorias;
    @FXML private Label lblTotal, lblTendencia, lblPromedio;
    @FXML private VBox vboxTopActividades;
    @FXML private ScrollPane scrollPrincipal;

    private final HuellaService huellaService = new HuellaService();
    private final HabitoService habitoService = new HabitoService();

    @FXML
    public void initialize() {
        if (Sesion.getInstancia().getUsuario() == null) return;

        cargarDatosResumen();
        cargarGraficoCategorias();
        cargarGraficoEvolucion();
        cargarTopActividades();
    }

    private void cargarDatosResumen() {
        int userId = Sesion.getInstancia().getUsuario().getId();
        List<Huella> huellas = huellaService.obtenerHuellasPorUsuario(userId);

        // Calcular Total
        double total = huellas.stream().mapToDouble(Huella::getValor).sum();
        lblTotal.setText(String.format("%.1f kg", total));

        // Calcular Promedio
        double promedio = huellas.isEmpty() ? 0 : total / huellas.size();
        lblPromedio.setText(String.format("%.1f kg", promedio));

        // Tendencia (Ejemplo simple: comparado con un valor estático o mes anterior)
        lblTendencia.setText("-2.5%"); // Esto se podría calcular comparando meses
    }

    private void cargarGraficoCategorias() {
        int userId = Sesion.getInstancia().getUsuario().getId();
        List<Huella> huellas = huellaService.obtenerHuellasPorUsuario(userId);

        // Agrupamos por el nombre de la categoría de la actividad
        Map<String, Double> porCategoria = huellas.stream()
                .collect(Collectors.groupingBy(
                        h -> h.getIdActividad().getIdCategoria().getNombre(),
                        Collectors.summingDouble(Huella::getValor)
                ));

        chartCategorias.getData().clear();
        porCategoria.forEach((nombre, suma) -> {
            chartCategorias.getData().add(new PieChart.Data(nombre, suma));
        });
    }

    private void cargarGraficoEvolucion() {
        int userId = Sesion.getInstancia().getUsuario().getId();
        List<Huella> huellas = huellaService.obtenerHuellasPorUsuario(userId);

        if (huellas == null || huellas.isEmpty()) return;

        // 1. Agrupamos y sumamos por Año-Mes
        Map<java.time.YearMonth, Double> porMesAño = huellas.stream()
                .collect(Collectors.groupingBy(
                        h -> java.time.YearMonth.from(h.getFecha()),
                        Collectors.summingDouble(Huella::getValor)
                ));

        // 2. Limpiamos el gráfico por completo
        chartEvolucion.getData().clear();

        // 3. Si el eje X es de categorías, vamos a forzar la actualización de las etiquetas
        if (chartEvolucion.getXAxis() instanceof CategoryAxis xAxis) {
            xAxis.setAnimated(false); // Desactivamos animación para evitar fallos visuales
            xAxis.getCategories().clear();
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("kg CO₂ por mes");

        // 4. Ordenamos cronológicamente y añadimos a la serie
        porMesAño.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    String etiqueta = entry.getKey().getMonth()
                            .getDisplayName(java.time.format.TextStyle.SHORT, new java.util.Locale("es", "ES"))
                            + " " + entry.getKey().getYear();

                    series.getData().add(new XYChart.Data<>(etiqueta, entry.getValue()));
                });

        // 5. Añadimos la serie y forzamos que se dibujen los puntos
        chartEvolucion.getData().add(series);
        chartEvolucion.setCreateSymbols(true);
    }

    private void cargarTopActividades() {
        vboxTopActividades.getChildren().clear();
        int userId = Sesion.getInstancia().getUsuario().getId();
        List<Huella> huellas = huellaService.obtenerHuellasPorUsuario(userId);

        // Obtenemos las 3 actividades que más han sumado
        huellas.stream()
                .collect(Collectors.groupingBy(h -> h.getIdActividad().getNombre(), Collectors.summingDouble(Huella::getValor)))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3)
                .forEach(entry -> {
                    Label lbl = new Label("🔥 " + entry.getKey() + ": " + String.format("%.1f", entry.getValue()) + " kg CO₂");
                    lbl.setStyle("-fx-font-size: 14px; -fx-padding: 5;");
                    vboxTopActividades.getChildren().add(lbl);
                });
    }

    // --- NAVEGACIÓN ---
    @FXML private void irAInicio() { cambiarEscena("/view/inicio.fxml"); }
    @FXML private void irAMisHuellas() { cambiarEscena("/view/mis_huellas.fxml"); }
    @FXML private void irAHabitos() { cambiarEscena("/view/habitos.fxml"); }
    @FXML
    private void irARecomendaciones() {
        cambiarEscena("/view/recomendaciones.fxml");
    }
    @FXML private void handleLogout() {
        Sesion.getInstancia().setUsuario(null);
        cambiarEscena("/view/login.fxml");
    }

    private void cambiarEscena(String fxml) {
        try {
            Stage stage = (Stage) lblTotal.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
