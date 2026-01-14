package controller;

import model.Recomendacion;
import services.RecomendacionService;
import java.util.List;

public class RecomendacionController {
    private final RecomendacionService recomendacionService = new RecomendacionService();

    public List<Recomendacion> getRecomendacionesGenerales(int idCat) {
        return recomendacionService.obtenerPorCategoria(idCat); // [cite: 21, 62]
    }
}
