package controller;

import model.Habito;
import model.Recomendacion;
import services.HabitoService;
import java.util.List;

public class HabitoController {
    private final HabitoService habitoService = new HabitoService();

    public void registrarHabito(Habito h) {
        habitoService.asignarHabito(h); // [cite: 15, 83]
    }

    public List<Recomendacion> getSugerencias(int idUsuario) {
        return habitoService.sugerirMejoras(idUsuario); // [cite: 16, 22]
    }

    public List<Habito> getMisHabitos(int idUsuario) {
        return habitoService.misHabitos(idUsuario); // [cite: 15]
    }
}