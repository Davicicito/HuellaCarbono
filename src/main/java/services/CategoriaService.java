package services;

import DAO.CategoriaDAO;
import model.Categoria;
import java.util.List;

/**
 * Servicio de lógica de negocio para la gestión de categorías.
 * Actúa como intermediario entre la capa de presentación y el acceso a datos,
 * facilitando la clasificación de actividades y la organización de los
 * filtros de usuario en la interfaz.
 */
public class CategoriaService {

    /**
     * Acceso a la capa de persistencia de categorías.
     */
    private final CategoriaDAO categoriaDAO;

    /**
     * Inicializa el servicio configurando su DAO correspondiente.
     */
    public CategoriaService() {
        this.categoriaDAO = new CategoriaDAO();
    }

    /**
     * Recupera el catálogo completo de categorías existentes (Transporte, Energía, etc.).
     * Este método permite que la aplicación gestione factores de emisión agrupados
     * y presente opciones de filtrado coherentes al usuario.
     * * @return Una lista de objetos {@link Categoria} con sus metadatos de impacto.
     */
    public List<Categoria> obtenerCategorias() {
        return categoriaDAO.listarTodas();
    }
}