package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Habito;
import services.ActividadService;
import services.HabitoService;
import utils.Sesion;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador para la gestión de hábitos sostenibles.
 * Se encarga de mostrar los compromisos actuales del usuario, calcular el ahorro
 * estimado de CO2 basado en la frecuencia de estos y sugerir nuevas acciones.
 */
public class HabitosViewController {

    @FXML private FlowPane flowHabitos;
    @FXML private HBox hboxSugerencias;
    @FXML private Label lblTotalHabitos;
    @FXML private Label lblImpactoEstimado;

    private final HabitoService habitoService = new HabitoService();

    /**
     * Configuración inicial al cargar la vista.
     * Recupera los hábitos existentes del usuario y genera el panel de sugerencias.
     */
    @FXML
    public void initialize() {
        cargarHabitosUsuario();
        cargarSugerenciasManuales();
    }

    /**
     * Obtiene los hábitos del usuario desde la base de datos y actualiza la interfaz.
     * Calcula dinámicamente el ahorro total proyectado y gestiona el estado visual
     * de la lista, incluyendo mensajes cuando no hay datos.
     */
    private void cargarHabitosUsuario() {
        if (Sesion.getInstancia().getUsuario() == null) return;
        int userId = Sesion.getInstancia().getUsuario().getId();

        List<Habito> habitos = habitoService.misHabitos(userId);

        Platform.runLater(() -> {
            if (lblTotalHabitos != null) {
                lblTotalHabitos.setText(String.valueOf(habitos.size()));
            }

            double ahorroTotal = 0;
            if (flowHabitos != null) {
                flowHabitos.getChildren().clear();

                if (habitos.isEmpty()) {
                    Label lblVacio = new Label("Aún no tienes hábitos. ¡Define tu rutina sostenible! 🌱");
                    lblVacio.setStyle("-fx-text-fill: #9ca3af; -fx-font-style: italic; -fx-padding: 30;");
                    flowHabitos.getChildren().add(lblVacio);
                } else {
                    for (Habito h : habitos) {
                        flowHabitos.getChildren().add(crearTarjetaPro(h));
                        // Cálculo del impacto basado en frecuencia y factor de emisión de la categoría
                        if (h.getIdActividad() != null && h.getIdActividad().getIdCategoria() != null) {
                            ahorroTotal += h.getFrecuencia() * h.getIdActividad().getIdCategoria().getFactorEmision();
                        }
                    }
                }
            }

            if (lblImpactoEstimado != null) {
                lblImpactoEstimado.setText(String.format("%.1f kg CO₂", ahorroTotal));
            }

            actualizarBotonesSugerencias(habitos);
        });
    }

    /**
     * Construye visualmente una tarjeta (Card) para representar un hábito.
     * Incluye los controles para editar el hábito o eliminarlo tras confirmación.
     * @param h El objeto hábito a renderizar.
     * @return Un nodo VBox con el diseño y eventos configurados.
     */
    private VBox crearTarjetaPro(Habito h) {
        VBox card = new VBox(10);
        card.getStyleClass().add("stat-card");
        card.setPrefWidth(300);
        card.setPadding(new javafx.geometry.Insets(15));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label(h.getIdActividad().getNombre());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnEdit = new Button("✏️");
        btnEdit.setStyle("-fx-background-color: transparent; -fx-text-fill: #059669; -fx-cursor: hand; -fx-font-size: 14px;");
        btnEdit.setOnAction(e -> abrirFormularioEditar(h));

        Button btnDelete = new Button("🗑");
        btnDelete.setStyle("-fx-background-color: transparent; -fx-text-fill: #ef4444; -fx-cursor: hand; -fx-font-size: 14px;");
        btnDelete.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Eliminar Hábito");
            alert.setHeaderText(null);
            alert.setContentText("¿Estás seguro de que quieres eliminar este hábito?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                habitoService.eliminar(h);
                cargarHabitosUsuario();
            }
        });

        header.getChildren().addAll(title, spacer, btnEdit, btnDelete);

        Label info = new Label(h.getFrecuencia() + " veces por " + h.getTipo());
        info.setStyle("-fx-text-fill: #71717a;");

        card.getChildren().addAll(header, info);
        return card;
    }

    /**
     * Genera una lista de acciones recomendadas cargadas desde el sistema.
     * Muestra actividades predefinidas que el usuario puede adoptar como nuevos hábitos.
     */
    private void cargarSugerenciasManuales() {
        if (hboxSugerencias == null) return;
        hboxSugerencias.getChildren().clear();

        ActividadService service = new ActividadService();
        List<model.Actividad> todas = service.obtenerTodas();

        todas.stream().limit(4).forEach(act -> {
            hboxSugerencias.getChildren().add(
                    crearTarjetaSugerencia(act.getNombre(), "Impacto positivo", "🌱", "bg-green")
            );
        });
    }

    /**
     * Gestiona el estado de los botones de sugerencia.
     * Deshabilita y marca como "Activo" aquellas sugerencias que el usuario ya tenga en su lista personal.
     * @param habitosActivos Lista de hábitos actuales del usuario para realizar el cruce de datos.
     */
    private void actualizarBotonesSugerencias(List<Habito> habitosActivos) {
        List<String> nombresActivos = habitosActivos.stream()
                .map(h -> h.getIdActividad().getNombre().toLowerCase())
                .collect(Collectors.toList());

        for (javafx.scene.Node node : hboxSugerencias.getChildren()) {
            if (node instanceof VBox) {
                VBox card = (VBox) node;
                HBox header = (HBox) card.getChildren().get(0);
                VBox textContent = (VBox) header.getChildren().get(1);
                Label lblTitulo = (Label) textContent.getChildren().get(0);
                Button btn = (Button) card.getChildren().get(1);

                if (nombresActivos.contains(lblTitulo.getText().toLowerCase())) {
                    btn.setText("Hábito activo ✅");
                    btn.setDisable(true);
                    btn.setOpacity(0.6);
                } else {
                    btn.setText("Agregar hábito");
                    btn.setDisable(false);
                    btn.setOpacity(1.0);
                }
            }
        }
    }

    /**
     * Crea el componente visual para una sugerencia de hábito.
     * Define el comportamiento del botón para abrir el formulario preconfigurado con dicha actividad.
     */
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

        btn.setOnAction(e -> {
            ActividadService actividadService = new ActividadService();
            List<model.Actividad> actividades = actividadService.obtenerTodas();

            model.Actividad actividadSugerida = actividades.stream()
                    .filter(a -> a.getNombre().trim().equalsIgnoreCase(titulo.trim()))
                    .findFirst()
                    .orElse(null);

            ejecutarAperturaFormulario(actividadSugerida, null);
        });

        card.getChildren().addAll(header, btn);
        return card;
    }

    @FXML private void abrirFormularioNuevoHabito() { ejecutarAperturaFormulario(null, null); }
    private void abrirFormularioEditar(Habito h) { ejecutarAperturaFormulario(null, h); }

    /**
     * Centraliza la apertura de la ventana modal para creación o edición de hábitos.
     * Carga el formulario secundario y le inyecta los datos necesarios según el contexto.
     * @param act Actividad sugerida (opcional, para nuevos hábitos).
     * @param habitoExistente Hábito a editar (opcional, para modo edición).
     */
    private void ejecutarAperturaFormulario(model.Actividad act, Habito habitoExistente) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/nuevo_habito.fxml"));
            VBox root = loader.load();
            NuevoHabitoController controller = loader.getController();

            if (habitoExistente != null) {
                controller.prepararEdicion(habitoExistente);
            } else if (act != null) {
                controller.setActividadInicial(act);
            }

            Stage stage = new Stage();
            stage.setTitle(habitoExistente == null ? "Nuevo Hábito" : "Editar Hábito");
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            if (getClass().getResource("/style.css") != null) {
                stage.getScene().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            }
            stage.showAndWait();
            cargarHabitosUsuario();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- MÉTODOS DE NAVEGACIÓN ---
    @FXML private void irAInicio() { cambiarEscena("/view/inicio.fxml"); }
    @FXML private void irAMisHuellas() { cambiarEscena("/view/mis_huellas.fxml"); }
    @FXML private void irAAnalisis() { cambiarEscena("/view/analisis.fxml"); }
    @FXML private void irARecomendaciones() { cambiarEscena("/view/recomendaciones.fxml"); }
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