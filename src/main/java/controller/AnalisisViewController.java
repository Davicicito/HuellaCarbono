package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
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
import java.io.PrintWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import javafx.stage.FileChooser;

/**
 * Controlador de la vista de Análisis.
 * Se encarga de transformar los datos brutos de la base de datos en información visual
 * mediante gráficos estadísticos y resúmenes numéricos del impacto ambiental.
 */
public class AnalisisViewController {

    @FXML
    private LineChart<String, Number> chartEvolucion;
    @FXML
    private PieChart chartCategorias;
    @FXML
    private Label lblTotal, lblTendencia, lblPromedio;
    @FXML
    private VBox vboxTopActividades;
    @FXML
    private ScrollPane scrollPrincipal;

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

    /**
     * Calcula y muestra las métricas generales.
     * CAMBIO: Ahora obtiene el impacto total (CO2) multiplicado por los factores de emisión.
     */
    private void cargarDatosResumen() {
        int userId = Sesion.getInstancia().getUsuario().getId();

        // Obtenemos el impacto total real (HQL SUM con multiplicación)
        double totalImpacto = huellaService.consultarImpactoTotal(userId);
        lblTotal.setText(String.format("%.1f kg CO₂", totalImpacto));

        List<Huella> huellas = huellaService.obtenerHuellasPorUsuario(userId);

        // El promedio ahora se basa en el impacto real de CO2
        double promedio = huellas.isEmpty() ? 0 : totalImpacto / huellas.size();
        lblPromedio.setText(String.format("%.1f kg", promedio));

        lblTendencia.setText("-2.5%");
    }

    /**
     * Genera el gráfico circular (PieChart) de distribución por categoría.
     * CAMBIO: Suma el impacto real (valor * factor) para cada categoría.
     */
    private void cargarGraficoCategorias() {
        int userId = Sesion.getInstancia().getUsuario().getId();
        List<Huella> huellas = huellaService.obtenerHuellasPorUsuario(userId);

        Map<String, Double> porCategoria = huellas.stream()
                .collect(Collectors.groupingBy(
                        h -> h.getIdActividad().getIdCategoria().getNombre(),
                        Collectors.summingDouble(h -> h.getValor() * h.getIdActividad().getIdCategoria().getFactorEmision())
                ));

        chartCategorias.getData().clear();
        porCategoria.forEach((nombre, suma) -> {
            chartCategorias.getData().add(new PieChart.Data(nombre, suma));
        });
    }

    /**
     * Muestra la evolución temporal del impacto.
     * CAMBIO: Los puntos del gráfico representan kg de CO₂ reales por mes.
     */
    private void cargarGraficoEvolucion() {
        int userId = Sesion.getInstancia().getUsuario().getId();
        List<Huella> huellas = huellaService.obtenerHuellasPorUsuario(userId);

        if (huellas == null || huellas.isEmpty()) return;

        Map<java.time.YearMonth, Double> porMesAño = huellas.stream()
                .collect(Collectors.groupingBy(
                        h -> java.time.YearMonth.from(h.getFecha()),
                        Collectors.summingDouble(h -> h.getValor() * h.getIdActividad().getIdCategoria().getFactorEmision())
                ));

        chartEvolucion.getData().clear();

        if (chartEvolucion.getXAxis() instanceof CategoryAxis xAxis) {
            xAxis.setAnimated(false);
            xAxis.getCategories().clear();
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("kg CO₂ por mes");

        porMesAño.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    String etiqueta = entry.getKey().getMonth()
                            .getDisplayName(java.time.format.TextStyle.SHORT, new java.util.Locale("es", "ES"))
                            + " " + entry.getKey().getYear();

                    series.getData().add(new XYChart.Data<>(etiqueta, entry.getValue()));
                });

        chartEvolucion.getData().add(series);
    }

    /**
     * Identifica las 3 actividades con mayor impacto de CO2.
     * CAMBIO: El ranking se basa en la contaminación real generada.
     */
    private void cargarTopActividades() {
        vboxTopActividades.getChildren().clear();
        int userId = Sesion.getInstancia().getUsuario().getId();
        List<Huella> huellas = huellaService.obtenerHuellasPorUsuario(userId);

        huellas.stream()
                .collect(Collectors.groupingBy(
                        h -> h.getIdActividad().getNombre(),
                        Collectors.summingDouble(h -> h.getValor() * h.getIdActividad().getIdCategoria().getFactorEmision())
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3)
                .forEach(entry -> {
                    Label lbl = new Label("🔥 " + entry.getKey() + ": " + String.format("%.1f", entry.getValue()) + " kg CO₂");
                    lbl.setStyle("-fx-font-size: 14px; -fx-padding: 5;");
                    vboxTopActividades.getChildren().add(lbl);
                });
    }

    /**
     * Exporta el historial a CSV.
     * MEJORA: Incluye BOM UTF-8 para compatibilidad total con Excel y acentos.
     */
    @FXML
    private void exportarCSV() {
        List<Huella> datos = huellaService.obtenerHistorial(Sesion.getInstancia().getUsuario().getId());

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Informe de Huella");
        fileChooser.setInitialFileName("mi_huella_carbono.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));

        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (FileOutputStream fos = new FileOutputStream(file);
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8))) {

                // BOM para Excel (Acentos correctos)
                fos.write(0xEF); fos.write(0xBB); fos.write(0xBF);

                writer.println("Fecha,Actividad,Categoria,Valor,Unidad,Impacto_kgCO2");

                for (Huella h : datos) {
                    writer.println(String.format("%s,%s,%s,%.2f,%s,%.2f",
                            h.getFecha(),
                            h.getIdActividad().getNombre(),
                            h.getIdActividad().getIdCategoria().getNombre(),
                            h.getValor(),
                            h.getUnidad(),
                            h.getValor() * h.getIdActividad().getIdCategoria().getFactorEmision()
                    ));
                }

                writer.flush();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("¡Datos exportados con éxito! 🚀");
                alert.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML private void irAInicio() { cambiarEscena("/view/inicio.fxml"); }
    @FXML private void irAMisHuellas() { cambiarEscena("/view/mis_huellas.fxml"); }
    @FXML private void irAHabitos() { cambiarEscena("/view/habitos.fxml"); }
    @FXML private void irARecomendaciones() { cambiarEscena("/view/recomendaciones.fxml"); }

    @FXML
    private void handleLogout() {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}