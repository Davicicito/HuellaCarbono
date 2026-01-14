package services;

import DAO.RecomendacionDAO;
import model.Recomendacion;
import java.util.List;

public class RecomendacionService {
    private final RecomendacionDAO recomendacionDAO = new RecomendacionDAO();

    /**
     * Lista recomendaciones filtradas por una categoría específica.
     */
    public List<Recomendacion> obtenerPorCategoria(int idCategoria) {
        return recomendacionDAO.listarPorCategoria(idCategoria);
    }
}