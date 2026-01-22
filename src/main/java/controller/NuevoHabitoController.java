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

/**
 * Controlador para la gestión de hábitos (creación y edición).
 * Esta clase actúa como un formulario dinámico que puede precargarse con datos
 * de una sugerencia o con la información de un hábito ya existente para su modificación.
 */
public class NuevoHabitoController {

    @FXML private ComboBox<Actividad> comboActividades;
    @FXML private TextField txtFrecuencia;
    @FXML private ComboBox<String> comboTipo;

    private final HabitoService habitoService = new HabitoService();
    private final ActividadService actividadService = new ActividadService();

    private Actividad actividadSugerida;
    private Habito habitoEnEdicion;

    /**
     * Inicializa el formulario configurando las opciones de frecuencia y
     * cargando el catálogo de actividades disponibles.
     * Si existe una actividad sugerida previamente, fuerza su selección en el desplegable.
     */
    @FXML
    public void initialize() {
        // Opciones estándar de periodicidad para el usuario
        comboTipo.getItems().setAll("Semanal", "Mensual", "Diario");

        // Carga del catálogo completo de actividades desde la base de datos
        List<Actividad> listaActividades = actividadService.obtenerTodas();
        comboActividades.getItems().setAll(listaActividades);

        // Sincronización visual: asegura que el combo muestre la actividad que motivó la apertura
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
     * Prepara el controlador para trabajar en modo edición.
     * Almacena el objeto hábito original y rellena los campos de texto y
     * desplegables con sus valores actuales.
     * @param h El objeto {@link Habito} que se desea modificar.
     */
    public void prepararEdicion(Habito h) {
        this.habitoEnEdicion = h;
        this.actividadSugerida = h.getIdActividad();

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
     * Define la actividad inicial del formulario antes de que se muestre la vista.
     * Se utiliza habitualmente cuando el usuario pulsa en una sugerencia de hábito.
     * @param actividad La actividad que se quiere preseleccionar.
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

    /**
     * Valida y procesa la persistencia del hábito.
     * Si existe un hábito en edición, actualiza sus valores; de lo contrario,
     * crea una nueva instancia vinculada al usuario actual y a la actividad elegida.
     * Realiza conversiones de tipos y gestiona posibles errores de formato numérico.
     */
    @FXML
    private void guardar() {
        // Verificación de integridad: todos los datos son obligatorios
        if (comboActividades.getValue() == null || comboTipo.getValue() == null || txtFrecuencia.getText().trim().isEmpty()) {
            mostrarAlerta("Campos incompletos", "Por favor, rellena todos los campos.");
            return;
        }

        try {
            // Lógica de reutilización: editar objeto existente o instanciar uno nuevo
            Habito h = (habitoEnEdicion != null) ? habitoEnEdicion : new Habito();

            // Sincronización de la clave primaria compuesta necesaria para Hibernate
            h.getId().setIdUsuario(Sesion.getInstancia().getUsuario().getId());
            h.getId().setIdActividad(comboActividades.getValue().getId());

            // Actualización de propiedades del modelo
            h.setIdUsuario(Sesion.getInstancia().getUsuario());
            h.setIdActividad(comboActividades.getValue());
            h.setFrecuencia(Integer.parseInt(txtFrecuencia.getText().trim()));
            h.setTipo(comboTipo.getValue());
            h.setUltimaFecha(LocalDate.now());

            // Delegación de la persistencia (merge) a la capa de servicio
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

    /**
     * Muestra una ventana emergente de información al usuario.
     * @param titulo Texto del título de la alerta.
     * @param mensaje Contenido detallado del aviso.
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Obtiene la referencia de la ventana actual y solicita su cierre.
     */
    @FXML
    private void cerrar() {
        Stage stage = (Stage) txtFrecuencia.getScene().getWindow();
        stage.close();
    }
}