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

public class HabitosViewController {

    @FXML private FlowPane flowHabitos;
    @FXML private HBox hboxSugerencias;
    @FXML private Label lblTotalHabitos;
    @FXML private Label lblImpactoEstimado;

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
            if (lblTotalHabitos != null) {
                lblTotalHabitos.setText(String.valueOf(habitos.size()));
            }

            double ahorroTotal = 0;
            if (flowHabitos != null) {
                flowHabitos.getChildren().clear();

                // CAMBIO 1: MENSAJE DE LISTA VACÍA
                if (habitos.isEmpty()) {
                    Label lblVacio = new Label("Aún no tienes hábitos. ¡Define tu rutina sostenible! 🌱");
                    lblVacio.setStyle("-fx-text-fill: #9ca3af; -fx-font-style: italic; -fx-padding: 30;");
                    flowHabitos.getChildren().add(lblVacio);
                } else {
                    for (Habito h : habitos) {
                        flowHabitos.getChildren().add(crearTarjetaPro(h));
                        if (h.getIdActividad() != null && h.getIdActividad().getIdCategoria() != null) {
                            ahorroTotal += h.getFrecuencia() * h.getIdActividad().getIdCategoria().getFactorEmision();
                        }
                    }
                }
            }

            if (lblImpactoEstimado != null) {
                lblImpactoEstimado.setText(String.format("%.1f kg CO₂", ahorroTotal));
            }

            // Refrescamos sugerencias para actualizar los botones de "Activo"
            actualizarBotonesSugerencias(habitos);
        });
    }

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

        // CAMBIO 2: BOTÓN DE EDITAR
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

    private void cargarSugerenciasManuales() {
        if (hboxSugerencias == null) return;
        hboxSugerencias.getChildren().clear();

        ActividadService service = new ActividadService();
        List<model.Actividad> todas = service.obtenerTodas();

        // Tomamos actividades representativas para sugerir
        todas.stream().limit(4).forEach(act -> {
            hboxSugerencias.getChildren().add(
                    crearTarjetaSugerencia(act.getNombre(), "Impacto positivo", "🌱", "bg-green")
            );
        });
    }

    // CAMBIO 3: LÓGICA DE BLOQUEO DE SUGERENCIAS
    private void actualizarBotonesSugerencias(List<Habito> habitosActivos) {
        List<String> nombresActivos = habitosActivos.stream()
                .map(h -> h.getIdActividad().getNombre().toLowerCase())
                .collect(Collectors.toList());

        for (javafx.scene.Node node : hboxSugerencias.getChildren()) {
            if (node instanceof VBox) {
                VBox card = (VBox) node;
                // Buscamos el label del título dentro de la tarjeta
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

    @FXML
    private void abrirFormularioNuevoHabito() {
        ejecutarAperturaFormulario(null, null);
    }

    private void abrirFormularioEditar(Habito h) {
        ejecutarAperturaFormulario(null, h);
    }

    /**
     * Lógica unificada para abrir el formulario (Crear o Editar)
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

    @FXML private void irAInicio() { cambiarEscena("/view/inicio.fxml"); }
    @FXML private void irAMisHuellas() { cambiarEscena("/view/mis_huellas.fxml"); }
    @FXML
    private void irAAnalisis() {
        cambiarEscena("/view/analisis.fxml");
    }
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