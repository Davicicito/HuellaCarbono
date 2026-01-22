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

/**
 * Controlador para la ventana modal de registro de nuevas huellas.
 * Facilita la entrada de datos manual por parte del usuario, gestionando la selección
 * de actividades desde la base de datos, la validación de valores numéricos y
 * la asignación de unidades de medida.
 */
public class NuevaHuellaController {

    @FXML private ComboBox<Actividad> comboActividades;
    @FXML private ComboBox<String> comboUnidades;
    @FXML private TextField txtValor;
    @FXML private DatePicker datePicker;

    private final HuellaService huellaService = new HuellaService();
    private final ActividadService actividadService = new ActividadService();

    /**
     * Prepara el formulario al abrir la ventana.
     * Configura los valores por defecto de fecha y unidades, y establece un
     * StringConverter para que el ComboBox de actividades sea legible, mostrando
     * nombres en lugar de referencias a objetos.
     */
    @FXML
    public void initialize() {
        // Inicialización de valores predefinidos y fecha actual
        comboUnidades.getItems().addAll("km", "kWh", "kg", "m³");
        comboUnidades.getSelectionModel().selectFirst();
        datePicker.setValue(LocalDate.now());

        // Configuración del conversor para representar objetos Actividad como texto
        comboActividades.setConverter(new StringConverter<Actividad>() {
            @Override
            public String toString(Actividad act) {
                return (act != null) ? act.getNombre() : "";
            }
            @Override
            public Actividad fromString(String string) { return null; }
        });

        // Carga de actividades disponibles desde el servicio de negocio
        List<Actividad> lista = actividadService.listarTodasLasActividades();
        if (lista != null) {
            comboActividades.getItems().addAll(lista);
        }
    }

    /**
     * Procesa y almacena el nuevo registro de huella.
     * Captura los datos introducidos, instancia un objeto Huella vinculándolo al
     * usuario actual en sesión y lo envía al servicio para su persistencia.
     */
    @FXML
    private void handleGuardar() {
        try {
            Actividad act = comboActividades.getValue();
            // Validación mínima para evitar registros incompletos o nulos
            if (act == null || txtValor.getText().isEmpty()) return;

            Huella h = new Huella();
            h.setIdUsuario(Sesion.getInstancia().getUsuario());
            h.setIdActividad(act);
            h.setValor(Float.parseFloat(txtValor.getText()));
            h.setUnidad(comboUnidades.getValue());
            h.setFecha(datePicker.getValue());

            huellaService.registrarNuevaHuella(h);
            handleCancelar(); // Cierra la ventana tras el éxito
        } catch (NumberFormatException e) {
            // Manejo de error si el valor numérico no es válido
            System.err.println("Error: El valor introducido no es un número válido.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Cierra la ventana actual sin guardar cambios.
     * Obtiene la referencia al Stage a través de cualquier componente del nodo.
     */
    @FXML
    private void handleCancelar() {
        ((Stage) txtValor.getScene().getWindow()).close();
    }
}