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
 * Este es el cerebro de la pantalla de Análisis.
 * Su trabajo es coger todos esos datos "feos" de la base de datos y convertirlos en
 * gráficos y numeritos que el usuario pueda entender fácilmente para ver cuánto contamina.
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

    /**
     * Este método se ejecuta automáticamente al abrir la pantalla.
     * Primero mira si hay alguien logueado (por seguridad) y luego dispara
     * toda la carga de datos para que los gráficos aparezcan llenos.
     */
    @FXML
    public void initialize() {
        if (Sesion.getInstancia().getUsuario() == null) return;

        cargarDatosResumen();
        cargarGraficoCategorias();
        cargarGraficoEvolucion();
        cargarTopActividades();
    }

    /**
     * Rellena las tarjetas de arriba con el impacto total de CO2.
     * No solo suma los números, sino que pide al servicio que haga la multiplicación
     * por el factor de emisión para que el dato sea real y científico.
     */
    private void cargarDatosResumen() {
        int userId = Sesion.getInstancia().getUsuario().getId();

        // Le pedimos al servicio el total calculado directamente desde la BBDD
        double totalImpacto = huellaService.consultarImpactoTotal(userId);
        lblTotal.setText(String.format("%.1f kg CO₂", totalImpacto));

        List<Huella> huellas = huellaService.obtenerHuellasPorUsuario(userId);

        // Sacamos la media de cuánto contamina cada acción que hace el usuario
        double promedio = huellas.isEmpty() ? 0 : totalImpacto / huellas.size();
        lblPromedio.setText(String.format("%.1f kg", promedio));

        lblTendencia.setText("-2.5%");
    }

    /**
     * Crea el "gráfico de quesito".
     * Agrupa todas las huellas por su categoría (Transporte, Energía...) y suma
     * el impacto real para que veamos de un vistazo qué es lo que más nos hace contaminar.
     */
    private void cargarGraficoCategorias() {
        int userId = Sesion.getInstancia().getUsuario().getId();
        List<Huella> huellas = huellaService.obtenerHuellasPorUsuario(userId);

        // Usamos streams para agrupar y multiplicar valor por factor de emisión sobre la marcha
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
     * Dibuja la línea de tiempo para ver si el usuario está mejorando o no.
     * Junta los datos por mes y año para que la gráfica no sea un lío y
     * los ordena cronológicamente para que la línea tenga sentido.
     */
    private void cargarGraficoEvolucion() {
        int userId = Sesion.getInstancia().getUsuario().getId();
        List<Huella> huellas = huellaService.obtenerHuellasPorUsuario(userId);

        if (huellas == null || huellas.isEmpty()) return;

        // Agrupamos por mes/año y calculamos el CO2 total de cada mes
        Map<java.time.YearMonth, Double> porMesAño = huellas.stream()
                .collect(Collectors.groupingBy(
                        h -> java.time.YearMonth.from(h.getFecha()),
                        Collectors.summingDouble(h -> h.getValor() * h.getIdActividad().getIdCategoria().getFactorEmision())
                ));

        chartEvolucion.getData().clear();

        // Limpiamos el eje X para que JavaFX no se queje al repintar
        if (chartEvolucion.getXAxis() instanceof CategoryAxis xAxis) {
            xAxis.setAnimated(false);
            xAxis.getCategories().clear();
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("kg CO₂ por mes");

        // Ordenamos los meses y les ponemos un nombre bonito (ej: ene. 2026)
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
     * Busca las 3 actividades que más han "castigado" al planeta.
     * Ordena las actividades por su impacto total de CO2 y las muestra en una
     * lista con un emoji de fuego para que el usuario sepa dónde tiene que recortar.
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
     * Exporta todo tu historial de contaminación a un archivo CSV.
     * Le hemos metido un truco técnico (BOM UTF-8) para que al abrirlo en Excel
     * los acentos y las eñes se vean perfectos y no como símbolos raros.
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

                // Escribimos la "firma" UTF-8 para que Excel entienda los acentos
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

    // Métodos para moverse entre las pantallas del menú lateral
    @FXML private void irAInicio() { cambiarEscena("/view/inicio.fxml"); }
    @FXML private void irAMisHuellas() { cambiarEscena("/view/mis_huellas.fxml"); }
    @FXML private void irAHabitos() { cambiarEscena("/view/habitos.fxml"); }
    @FXML private void irARecomendaciones() { cambiarEscena("/view/recomendaciones.fxml"); }

    /**
     * Cierra la sesión borrando al usuario de la memoria y te manda de vuelta al Login.
     */
    @FXML
    private void handleLogout() {
        Sesion.getInstancia().setUsuario(null);
        cambiarEscena("/view/login.fxml");
    }

    /**
     * Cambia la ventana actual por otra,
     * asegurándose de cargar el CSS para que la nueva pantalla no se vea mal.
     */
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