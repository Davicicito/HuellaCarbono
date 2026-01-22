package DAO;

import connection.Connection;
import model.Recomendacion;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;

/**
 * Clase de acceso a datos (DAO) para la entidad Recomendacion.
 * Se encarga de extraer de la base de datos los consejos y acciones sugeridas
 * para reducir la huella de carbono, permitiendo un filtrado temático basado
 * en las categorías de actividad.
 */
public class RecomendacionDAO {

    /**
     * Consulta HQL para filtrar recomendaciones según el identificador de su categoría.
     */
    private static final String LISTAR_POR_CATEGORIA = "FROM Recomendacion WHERE idCategoria.id = :idCat";

    /**
     * Recupera una lista de recomendaciones vinculadas a una categoría específica.
     * Utiliza parámetros nombrados en la consulta para prevenir ataques de
     * inyección y asegurar una correcta tipificación de los datos.
     * * @param idCategoria Identificador único de la categoría (ej: Transporte, Energía).
     * @return Una lista de objetos {@link Recomendacion} que coinciden con el criterio,
     * o {@code null} si ocurre una anomalía en la conexión.
     */
    public List<Recomendacion> listarPorCategoria(int idCategoria) {
        // Apertura de sesión mediante el Singleton de conexión
        try (Session session = Connection.getInstance().openSession()) {
            Query<Recomendacion> query = session.createQuery(LISTAR_POR_CATEGORIA, Recomendacion.class);
            // Asignación segura del parámetro de filtrado
            query.setParameter("idCat", idCategoria);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error al filtrar recomendaciones por categoría: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}