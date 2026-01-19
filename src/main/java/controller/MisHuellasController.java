package controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Huella;
import model.Usuario;
import model.Categoria;
import services.HuellaService;
import services.CategoriaService;
import utils.Sesion;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class MisHuellasController {

    @FXML private TableView<Huella> tablaHuellas;
    @FXML private TableColumn<Huella, String> colActividad;
    @FXML private TableColumn<Huella, String> colValor;
    @FXML private TableColumn<Huella, String> colCategoria;
    @FXML private TableColumn<Huella, String> colFecha;
    @FXML private TableColumn<Huella, String> colImpacto;
    @FXML private TableColumn<Huella, Void> colAcciones;

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> comboCategorias;

    private final HuellaService huellaService = new HuellaService();
    private final CategoriaService categoriaService = new CategoriaService();

    private ObservableList<Huella> listaMaestra = FXCollections.observableArrayList();
    private FilteredList<Huella> listaFiltrada;

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarComboCategorias();
        configurarFiltros();
        cargarDatos();
    }

    private void configurarColumnas() {
        colActividad.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getIdActividad() != null ?
                        cellData.getValue().getIdActividad().getNombre() : "N/A"));

        colValor.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getValor() + " " + cellData.getValue().getUnidad()));

        // CATEGORÍAS CON DISEÑO DE BADGE (Colores dinámicos)
        colCategoria.setCellFactory(column -> new TableCell<Huella, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label label = new Label(item);
                    label.getStyleClass().add("category-badge");
                    String lower = item.toLowerCase();
                    if (lower.contains("transporte")) label.getStyleClass().add("badge-transporte");
                    else if (lower.contains("energía") || lower.contains("energia")) label.getStyleClass().add("badge-energia");
                    else if (lower.contains("alimentación") || lower.contains("alimentacion")) label.getStyleClass().add("badge-alimentacion");
                    setGraphic(label);
                }
            }
        });
        colCategoria.setCellValueFactory(cellData -> {
            if (cellData.getValue().getIdActividad() != null && cellData.getValue().getIdActividad().getIdCategoria() != null) {
                return new SimpleStringProperty(cellData.getValue().getIdActividad().getIdCategoria().getNombre());
            }
            return new SimpleStringProperty("-");
        });

        colFecha.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getFecha() != null ? cellData.getValue().getFecha().toString() : ""));

        colImpacto.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getValor() + " kg CO₂"));
        colImpacto.getStyleClass().add("impacto-bold");

        // COLUMNA DE ACCIONES (Borrar registro)
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEliminar = new Button("🗑");
            {
                btnEliminar.getStyleClass().add("sidebar-button-exit");
                btnEliminar.setOnAction(event -> {
                    Huella h = getTableView().getItems().get(getIndex());
                    confirmarEliminacion(h);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnEliminar);
            }
        });
    }

    private void confirmarEliminacion(Huella h) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar Registro");
        alert.setHeaderText("¿Seguro que quieres borrar esta huella?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            huellaService.borrarRegistro(h);
            cargarDatos();
        }
    }

    private void cargarComboCategorias() {
        comboCategorias.getItems().clear();
        comboCategorias.getItems().add("Todas las categorías");
        List<Categoria> categorias = categoriaService.obtenerCategorias();
        if (categorias != null) {
            for (Categoria cat : categorias) comboCategorias.getItems().add(cat.getNombre());
        }
        comboCategorias.getSelectionModel().selectFirst();
    }

    private void configurarFiltros() {
        listaFiltrada = new FilteredList<>(listaMaestra, p -> true);
        txtBuscar.textProperty().addListener((obs, old, nv) -> aplicarFiltros());
        comboCategorias.valueProperty().addListener((obs, old, nv) -> aplicarFiltros());
        tablaHuellas.setItems(listaFiltrada);
    }

    private void aplicarFiltros() {
        String texto = txtBuscar.getText().toLowerCase().trim();
        String cat = comboCategorias.getValue();
        listaFiltrada.setPredicate(h -> {
            boolean coincideT = texto.isEmpty() || h.getIdActividad().getNombre().toLowerCase().contains(texto);
            boolean coincideC = cat == null || cat.equals("Todas las categorías") ||
                    (h.getIdActividad().getIdCategoria() != null && h.getIdActividad().getIdCategoria().getNombre().equals(cat));
            return coincideT && coincideC;
        });
    }

    private void cargarDatos() {
        Usuario u = Sesion.getInstancia().getUsuario();
        if (u != null) {
            List<Huella> lista = huellaService.obtenerHistorial(u.getId());
            Platform.runLater(() -> listaMaestra.setAll(lista));
        }
    }

    @FXML private void abrirFormularioNuevaHuella() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/nueva_huella.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            cargarDatos();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML private void irAInicio() { cambiarEscena("/view/inicio.fxml"); }
    @FXML private void irAHabitos() { cambiarEscena("/view/habitos.fxml"); }
    @FXML
    private void irAAnalisis() {
        cambiarEscena("/view/analisis.fxml");
    }
    @FXML private void handleLogout() { Sesion.getInstancia().setUsuario(null); cambiarEscena("/view/login.fxml"); }

    private void cambiarEscena(String fxml) {
        try {
            Stage stage = (Stage) tablaHuellas.getScene().getWindow();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource(fxml)));
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) { e.printStackTrace(); }
    }
}