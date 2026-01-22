package DAO;

import connection.Connection;
import model.Actividad;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

/**
 * Clase de acceso a datos (DAO) para la entidad Actividad.
 * Proporciona los métodos necesarios para consultar el catálogo de actividades
 * disponibles en la base de datos, permitiendo que el usuario seleccione
 * acciones específicas para registrar su huella o definir hábitos.
 */
public class ActividadDAO {

    /**
     * Consulta HQL para obtener todas las entidades de tipo Actividad.
     */
    private static final String LISTAR_TODAS = "FROM Actividad";

    /**
     * Recupera el listado completo de actividades almacenadas en el sistema.
     * Este método es fundamental para poblar los componentes de selección (ComboBox)
     * en los formularios de la interfaz de usuario.
     * * @return Una lista de objetos {@link Actividad} o {@code null} si ocurre un error en la consulta.
     */
    public List<Actividad> listarTodas() {
        // Abrimos la sesión a través de nuestra clase de conexión personalizada
        try (Session session = Connection.getInstance().openSession()) {
            // Creamos la consulta tipada utilizando HQL
            Query<Actividad> query = session.createQuery(LISTAR_TODAS, Actividad.class);
            return query.getResultList();
        } catch (Exception e) {
            // Log de error técnico en consola para depuración
            System.err.println("Error al recuperar el catálogo de actividades: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}