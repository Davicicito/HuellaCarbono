package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Huella;
import model.Usuario;
import services.HuellaService;
import utils.Sesion;
import java.io.IOException;
import java.util.List;

public class MisHuellasController {

    @FXML private TableView<Huella> tablaHuellas;
    @FXML private TableColumn<Huella, String> colActividad;
    @FXML private TableColumn<Huella, Float> colValor;
    @FXML private TableColumn<Huella, String> colCategoria;
    @FXML private TableColumn<Huella, String> colFecha;
    @FXML private TableColumn<Huella, String> colImpacto;

    private final HuellaService huellaService = new HuellaService();

    @FXML
    public void initialize() {
        // 1. Columna Actividad (Accedemos al objeto Actividad y sacamos el nombre)
        colActividad.setCellValueFactory(cellData -> {
            if (cellData.getValue().getIdActividad() != null) {
                return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getIdActividad().getNombre());
            } else {
                return new javafx.beans.property.SimpleStringProperty("Sin nombre");
            }
        });

        // 2. Columna Valor (Asegúrate de que en la clase Huella el campo se llame "valor")
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));

        // 3. Columna Categoría (Accedemos a la Actividad -> Categoría -> Nombre)
        colCategoria.setCellValueFactory(cellData -> {
            if (cellData.getValue().getIdActividad() != null && cellData.getValue().getIdActividad().getIdCategoria() != null) {
                return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getIdActividad().getIdCategoria().getNombre());
            }
            return new javafx.beans.property.SimpleStringProperty("-");
        });

        // 4. Columna Fecha
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));

        // 5. Columna Impacto (Cálculo rápido para ver algo en la tabla)
        colImpacto.setCellValueFactory(cellData -> {
            float total = cellData.getValue().getValor(); // Aquí podrías multiplicar por el factor si lo tienes
            return new javafx.beans.property.SimpleStringProperty(total + " kg CO2");
        });

        // CARGAR DATOS AL INICIAR
        cargarDatos();
    }

    private void cargarDatos() {
        Usuario usuarioActual = Sesion.getInstancia().getUsuario();
        if (usuarioActual != null) {
            List<Huella> lista = huellaService.obtenerHistorial(usuarioActual.getId());

            System.out.println("DEBUG: Registros encontrados en BD: " + lista.size()); // Mira esto en la consola

            ObservableList<Huella> datos = FXCollections.observableArrayList(lista);
            tablaHuellas.setItems(datos);
            tablaHuellas.refresh();
        } else {
            System.out.println("DEBUG: El usuario en sesión es NULO");
        }
    }

    @FXML
    private void irAInicio() {
        try {
            Stage stage = (Stage) tablaHuellas.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/inicio.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("EcoTrack - Inicio");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() throws IOException {
        cambiarEscena("/view/login.fxml");
    }

    @FXML
    private void abrirFormularioNuevaHuella() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/nueva_huella.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Nueva Huella");
            stage.setScene(scene);

            // El programa se detiene aquí hasta que cierras la ventana de "Guardar"
            stage.showAndWait();

            // ESTA LÍNEA ES LA CLAVE: Vuelve a consultar la BD y refresca la tabla
            cargarDatos();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void cambiarEscena(String fxml) throws IOException {
        Stage stage = (Stage) tablaHuellas.getScene().getWindow();
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource(fxml)));
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
    }
}