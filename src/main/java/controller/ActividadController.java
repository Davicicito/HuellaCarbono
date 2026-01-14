package controller;

import model.Actividad;
import services.ActividadService;
import java.util.List;

public class ActividadController {
    private final ActividadService actividadService = new ActividadService();

    public List<Actividad> listarActividades() {
        return actividadService.listarTodasLasActividades(); // [cite: 14, 59]
    }
}