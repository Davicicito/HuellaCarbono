package controller;

import model.Categoria;
import services.CategoriaService;
import java.util.List;

public class CategoriaController {
    private final CategoriaService categoriaService = new CategoriaService();

    public List<Categoria> listarCategorias() {
        return categoriaService.obtenerCategorias(); // [cite: 60]
    }
}
