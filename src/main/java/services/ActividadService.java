package services;

import DAO.ActividadDAO;
import model.Actividad;
import java.util.List;

/**
 * Servicio encargado de gestionar la lógica de negocio para las actividades.
 * Proporciona una capa de abstracción sobre el DAO de Actividades, facilitando
 * la obtención del catálogo de acciones que el usuario puede realizar para
 * registrar su impacto ambiental.
 */
public class ActividadService {

    /**
     * Instancia del DAO de Actividad para la interacción con la persistencia.
     */
    private final ActividadDAO actividadDAO;

    /**
     * Constructor por defecto que inicializa el acceso a datos.
     */
    public ActividadService() {
        this.actividadDAO = new ActividadDAO();
    }

    /**
     * Recupera el listado completo de actividades registradas en el sistema.
     * Este método es utilizado por los controladores para poblar los elementos
     * de selección y las tablas de consulta.
     * * @return Una lista de objetos {@link Actividad} con todas las opciones disponibles.
     */
    public List<Actividad> listarTodasLasActividades() {
        return actividadDAO.listarTodas();
    }

    /**
     * Método de conveniencia para recuperar todas las actividades disponibles.
     * Diseñado específicamente para alimentar componentes visuales como ComboBox
     * en formularios de creación de huellas o hábitos.
     * * @return Lista exhaustiva de actividades del catálogo.
     */
    public List<Actividad> obtenerTodas() {
        return actividadDAO.listarTodas();
    }
}