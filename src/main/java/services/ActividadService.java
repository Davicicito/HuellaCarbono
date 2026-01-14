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
}
