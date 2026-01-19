package services;

import DAO.ActividadDAO;
import model.Actividad;
import java.util.List;

public class ActividadService {
    private final ActividadDAO actividadDAO;

    public ActividadService() {
        this.actividadDAO = new ActividadDAO();
    }

    public List<Actividad> listarTodasLasActividades() {
        return actividadDAO.listarTodas();
    }

    /**
     * Recupera todas las actividades disponibles para el ComboBox.
     */
    public List<Actividad> obtenerTodas() {
        return actividadDAO.listarTodas();
    }
}
