package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Actividad;
import model.Habito;
import services.HabitoService;
import services.ActividadService;
import utils.Sesion;
import java.time.LocalDate;
import java.util.List;

public class NuevoHabitoController {

    @FXML private ComboBox<Actividad> comboActividades;
    @FXML private TextField txtFrecuencia;
    @FXML private ComboBox<String> comboTipo;

    private final HabitoService habitoService = new HabitoService();
    private final ActividadService actividadService = new ActividadService();

    private Actividad actividadSugerida;
    private Habito habitoEnEdicion;

    @FXML
    public void initialize() {
        // 1. Cargamos los tipos de frecuencia
        comboTipo.getItems().setAll("Semanal", "Mensual", "Diario");

        // 2. Cargamos todas las actividades de la BD
        List<Actividad> listaActividades = actividadService.obtenerTodas();
        comboActividades.getItems().setAll(listaActividades);

        // 3. Sincronización de selección inicial
        if (actividadSugerida != null) {
            Platform.runLater(() -> {
                for (Actividad a : comboActividades.getItems()) {
                    if (a.getId().equals(actividadSugerida.getId())) {
                        comboActividades.getSelectionModel().select(a);
                        comboActividades.setValue(a);
                        break;
                    }
                }
            });
        }
    }

    /**
     * Configura el formulario para editar un hábito existente
     */
    public void prepararEdicion(Habito h) {
        this.habitoEnEdicion = h;
        this.actividadSugerida = h.getIdActividad();

        // Rellenamos los campos con los datos actuales del hábito
        Platform.runLater(() -> {
            if (txtFrecuencia != null) {
                txtFrecuencia.setText(String.valueOf(h.getFrecuencia()));
            }
            if (comboTipo != null) {
                comboTipo.setValue(h.getTipo());
            }
        });
    }

    /**
     * Recibe una actividad para pre-cargar el formulario (desde sugerencias)
     */
    public void setActividadInicial(Actividad actividad) {
        this.actividadSugerida = actividad;

        if (comboActividades != null && actividad != null) {
            for (Actividad a : comboActividades.getItems()) {
                if (a.getId().equals(actividad.getId())) {
                    comboActividades.getSelectionModel().select(a);
                    comboActividades.setValue(a);
                    break;
                }
            }
        }
    }

    @FXML
    private void guardar() {
        // Validación básica de campos
        if (comboActividades.getValue() == null || comboTipo.getValue() == null || txtFrecuencia.getText().trim().isEmpty()) {
            mostrarAlerta("Campos incompletos", "Por favor, rellena todos los campos.");
            return;
        }

        try {
            // SI estamos editando, usamos el objeto existente. SI NO, creamos uno nuevo.
            Habito h = (habitoEnEdicion != null) ? habitoEnEdicion : new Habito();

            // Sincronizamos la clave primaria compuesta (HabitoId)
            h.getId().setIdUsuario(Sesion.getInstancia().getUsuario().getId());
            h.getId().setIdActividad(comboActividades.getValue().getId());

            // Rellenamos los datos del objeto
            h.setIdUsuario(Sesion.getInstancia().getUsuario());
            h.setIdActividad(comboActividades.getValue());
            h.setFrecuencia(Integer.parseInt(txtFrecuencia.getText().trim()));
            h.setTipo(comboTipo.getValue());
            h.setUltimaFecha(LocalDate.now());

            // El service llamará al DAO (usando merge) para persistir los cambios
            if (habitoService.guardar(h)) {
                System.out.println("Hábito procesado con éxito.");
                cerrar();
            } else {
                mostrarAlerta("Error", "No se pudo guardar el hábito en la base de datos.");
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato", "La frecuencia debe ser un número entero.");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error Crítico", "Ocurrió un fallo inesperado: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void cerrar() {
        Stage stage = (Stage) txtFrecuencia.getScene().getWindow();
        stage.close();
    }
}