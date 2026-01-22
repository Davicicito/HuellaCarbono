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

/**
 * Controlador de la vista de Análisis.
 * Se encarga de transformar los datos brutos de la base de datos en información visual
 * mediante gráficos estadísticos y resúmenes numéricos del impacto ambiental.
 */
public class AnalisisViewController {

    @FXML private LineChart<String, Number> chartEvolucion;
    @FXML private PieChart chartCategorias;
    @FXML private Label lblTotal, lblTendencia, lblPromedio;
    @FXML private VBox vboxTopActividades;
    @FXML private ScrollPane scrollPrincipal;

    private final HuellaService huellaService = new HuellaService();
    private final HabitoService habitoService = new HabitoService();

    /**
     * Inicializa la pantalla tras cargar el FXML.
     * Verifica que haya un usuario en sesión y lanza la carga de todos los componentes visuales.
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
     * Calcula y muestra las métricas generales en las tarjetas superiores.
     * Obtiene el acumulado total de CO2 y el promedio por registro del usuario.
     */
    private void cargarDatosResumen() {
        int userId = Sesion.getInstancia().getUsuario().getId();
        List<Huella> huellas = huellaService.obtenerHuellasPorUsuario(userId);

        // Sumamos todos los valores de las huellas registradas
        double total = huellas.stream().mapToDouble(Huella::getValor).sum();
        lblTotal.setText(String.format("%.1f kg", total));

        // Calculamos la media aritmética simple
        double promedio = huellas.isEmpty() ? 0 : total / huellas.size();
        lblPromedio.setText(String.format("%.1f kg", promedio));

        // Valor estático temporal para la tendencia
        lblTendencia.setText("-2.5%");
    }

    /**
     * Genera el gráfico circular (PieChart) de distribución por categoría.
     * Agrupa los registros por el nombre de su categoría y suma sus valores
     * para mostrar qué áreas (Transporte, Energía, etc.) pesan más en la huella.
     */
    private void cargarGraficoCategorias() {
        int userId = Sesion.getInstancia().getUsuario().getId();
        List<Huella> huellas = huellaService.obtenerHuellasPorUsuario(userId);

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

    /**
     * Gestiona el gráfico de líneas para mostrar la evolución temporal.
     * Agrupa los datos por mes y año, asegurando un orden cronológico en el eje X
     * para que la línea de tendencia refleje correctamente el paso del tiempo.
     */
    private void cargarGraficoEvolucion() {
        int userId = Sesion.getInstancia().getUsuario().getId();
        List<Huella> huellas = huellaService.obtenerHuellasPorUsuario(userId);

        if (huellas == null || huellas.isEmpty()) return;

        // Agrupamos usando YearMonth para no mezclar meses de distintos años
        Map<java.time.YearMonth, Double> porMesAño = huellas.stream()
                .collect(Collectors.groupingBy(
                        h -> java.time.YearMonth.from(h.getFecha()),
                        Collectors.summingDouble(Huella::getValor)
                ));

        chartEvolucion.getData().clear();

        // Forzamos la limpieza de categorías para evitar fallos de renderizado en JavaFX
        if (chartEvolucion.getXAxis() instanceof CategoryAxis xAxis) {
            xAxis.setAnimated(false);
            xAxis.getCategories().clear();
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("kg CO₂ por mes");

        // Ordenamos las llaves (fechas) y creamos los puntos del gráfico
        porMesAño.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    String etiqueta = entry.getKey().getMonth()
                            .getDisplayName(java.time.format.TextStyle.SHORT, new java.util.Locale("es", "ES"))
                            + " " + entry.getKey().getYear();

                    series.getData().add(new XYChart.Data<>(etiqueta, entry.getValue()));
                });

        chartEvolucion.getData().add(series);
        chartEvolucion.setCreateSymbols(true);
    }

    /**
     * Identifica las 3 actividades con mayor impacto negativo.
     * Realiza un ranking de las actividades que más CO2 han generado y las
     * muestra en un listado vertical de forma dinámica.
     */
    private void cargarTopActividades() {
        vboxTopActividades.getChildren().clear();
        int userId = Sesion.getInstancia().getUsuario().getId();
        List<Huella> huellas = huellaService.obtenerHuellasPorUsuario(userId);

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

    // --- MÉTODOS DE NAVEGACIÓN ---

    @FXML private void irAInicio() { cambiarEscena("/view/inicio.fxml"); }
    @FXML private void irAMisHuellas() { cambiarEscena("/view/mis_huellas.fxml"); }
    @FXML private void irAHabitos() { cambiarEscena("/view/habitos.fxml"); }
    @FXML private void irARecomendaciones() { cambiarEscena("/view/recomendaciones.fxml"); }

    /**
     * Cierra la sesión del usuario actual y redirige a la pantalla de Login.
     */
    @FXML private void handleLogout() {
        Sesion.getInstancia().setUsuario(null);
        cambiarEscena("/view/login.fxml");
    }

    /**
     * Método genérico para el intercambio de escenas en la aplicación.
     * Carga el archivo FXML solicitado y le aplica la hoja de estilos global.
     * @param fxml Ruta del archivo de vista a cargar.
     */
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