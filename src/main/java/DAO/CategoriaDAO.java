package DAO;

import connection.Connection;
import model.Categoria;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;

/**
 * Clase de acceso a datos (DAO) para la entidad Categoria.
 * Se encarga de gestionar las consultas relacionadas con las grandes áreas de
 * impacto ambiental (como Alimentación, Transporte o Energía), permitiendo
 * organizar y filtrar los datos en la interfaz y los gráficos.
 */
public class CategoriaDAO {

    /**
     * Consulta HQL para recuperar todas las categorías de la base de datos.
     */
    private static final String LISTAR_TODAS = "FROM Categoria";

    /**
     * Obtiene la lista completa de categorías disponibles.
     * Se utiliza principalmente para llenar los filtros de búsqueda y los
     * selectores en las pantallas de registro de huella y análisis.
     * * @return Una lista de objetos {@link Categoria} con su información
     * y factores de emisión asociados.
     */
    public List<Categoria> listarTodas() {
        // Utilizamos la sesión de Hibernate para realizar la consulta sobre el modelo de objetos
        try (Session session = Connection.getInstance().openSession()) {
            Query<Categoria> query = session.createQuery(LISTAR_TODAS, Categoria.class);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error al listar las categorías: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}