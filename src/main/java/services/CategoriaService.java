package services;

import DAO.CategoriaDAO;
import model.Categoria;
import java.util.List;

public class CategoriaService {
    private final CategoriaDAO categoriaDAO;

    public CategoriaService() {
        this.categoriaDAO = new CategoriaDAO();
    }

    public List<Categoria> obtenerCategorias() {
        return categoriaDAO.listarTodas();
    }
}
