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

    private final HuellaService huellaService = new HuellaService();
    private final ActividadService actividadService = new ActividadService();

    @FXML
    public void initialize() {
        // 1. Unidades por defecto
        comboUnidades.getItems().addAll("km", "kWh", "kg", "m³");
        comboUnidades.getSelectionModel().selectFirst();
        datePicker.setValue(LocalDate.now());

        // 2. Configurar el ComboBox para que muestre el NOMBRE de la actividad
        comboActividades.setConverter(new StringConverter<Actividad>() {
            @Override
            public String toString(Actividad act) {
                return (act != null) ? act.getNombre() : "";
            }
            @Override
            public Actividad fromString(String string) { return null; }
        });

        // 3. Cargar las actividades de tu base de datos
        List<Actividad> lista = actividadService.listarTodasLasActividades();
        if (lista != null) {
            comboActividades.getItems().addAll(lista);
        }
    }

    @FXML
    private void handleGuardar() {
        try {
            Actividad act = comboActividades.getValue();
            if (act == null || txtValor.getText().isEmpty()) return;

            Huella h = new Huella();
            h.setIdUsuario(Sesion.getInstancia().getUsuario()); //
            h.setIdActividad(act);
            h.setValor(Float.parseFloat(txtValor.getText()));
            h.setUnidad(comboUnidades.getValue());
            h.setFecha(datePicker.getValue());

            huellaService.registrarNuevaHuella(h); //
            handleCancelar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelar() {
        ((Stage) txtValor.getScene().getWindow()).close();
    }
}