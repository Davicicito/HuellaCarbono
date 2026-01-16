package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Actividad;
import model.Huella;
import services.ActividadService;
import services.HuellaService;
import utils.Sesion;
import java.time.LocalDate;
import java.util.List;

public class NuevaHuellaController {

    @FXML private ComboBox<Actividad> comboActividades;
    @FXML private ComboBox<String> comboUnidades;
    @FXML private TextField txtValor;
    @FXML private DatePicker datePicker;

    // Usamos tus servicios existentes
    private final HuellaService huellaService = new HuellaService();
    private final ActividadService actividadService = new ActividadService();

    @FXML
    public void initialize() {
        // 1. Llenar el desplegable de UNIDADES
        comboUnidades.getItems().addAll("km", "kWh", "kg", "m3", "litros");
        comboUnidades.getSelectionModel().selectFirst();

        // 2. Fecha de hoy por defecto
        datePicker.setValue(LocalDate.now());

        // 3. Configurar el ComboBox de ACTIVIDADES para que muestre el NOMBRE
        comboActividades.setConverter(new StringConverter<Actividad>() {
            @Override
            public String toString(Actividad act) {
                return (act != null) ? act.getNombre() : "";
            }
            @Override
            public Actividad fromString(String string) {
                return null;
            }
        });

        // 4. Cargar las actividades usando TU MÉTODO: listarTodasLasActividades()
        try {
            List<Actividad> lista = actividadService.listarTodasLasActividades();
            if (lista != null && !lista.isEmpty()) {
                comboActividades.getItems().addAll(lista);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar actividades: " + e.getMessage());
        }
    }

    @FXML
    private void handleGuardar() {
        try {
            // Validación básica
            if (comboActividades.getValue() == null || txtValor.getText().isEmpty()) {
                mostrarAlerta("Campos incompletos", "Por favor, selecciona una actividad y escribe un valor.");
                return;
            }

            // Creamos el objeto Huella con los datos del formulario
            Huella h = new Huella();
            h.setIdUsuario(Sesion.getInstancia().getUsuario());
            h.setIdActividad(comboActividades.getValue());
            h.setValor(Float.parseFloat(txtValor.getText().replace(",", ".")));
            h.setUnidad(comboUnidades.getValue());
            h.setFecha(datePicker.getValue());

            // Guardamos en la BD
            huellaService.registrarNuevaHuella(h);

            // Cerramos la ventana
            handleCancelar();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de número", "El valor introducido no es válido.");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo guardar la huella.");
        }
    }

    @FXML
    private void handleCancelar() {
        Stage stage = (Stage) txtValor.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}