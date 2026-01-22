package DAO;

import connection.Connection;
import model.Huella;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

/**
 * Clase de acceso a datos (DAO) para la entidad Huella.
 * Es el componente principal para la gestión del impacto ambiental del usuario.
 * Implementa operaciones CRUD y consultas avanzadas que integran lógica aritmética
 * (factores de emisión) directamente en las peticiones a la base de datos.
 */
public class HuellaDAO {

    /**
     * Consulta HQL con carga forzada (JOIN FETCH).
     * Recupera el historial completo del usuario incluyendo las relaciones de
     * actividad y categoría para evitar consultas adicionales durante el
     * renderizado de tablas y gráficos.
     */
    private static final String LISTAR_POR_USUARIO_COMPLETO =
            "SELECT h FROM Huella h " +
                    "JOIN FETCH h.idActividad a " +
                    "JOIN FETCH a.idCategoria " +
                    "WHERE h.idUsuario.id = :idUsuario " +
                    "ORDER BY h.fecha DESC";

    /**
     * Consulta HQL para el cálculo del impacto total.
     * Multiplica el valor registrado por el factor de emisión de su categoría
     * correspondiente de forma agregada.
     */
    private static final String SUMA_IMPACTO_TOTAL =
            "SELECT SUM(h.valor * h.idActividad.idCategoria.factorEmision) " +
                    "FROM Huella h WHERE h.idUsuario.id = :idUsuario";

    /**
     * Persiste un nuevo registro de huella en la base de datos.
     * Gestiona su propia transacción para asegurar la integridad de la operación.
     * @param huella Objeto {@link Huella} con los datos de consumo.
     */
    public void guardar(Huella huella) {
        Transaction tx = null;
        try (Session session = Connection.getInstance().openSession()) {
            tx = session.beginTransaction();
            session.persist(huella);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    /**
     * Actualiza un registro existente mediante la sincronización de estados (merge).
     * @param huella Registro de huella modificado.
     */
    public void actualizar(Huella huella) {
        Transaction tx = null;
        try (Session session = Connection.getInstance().openSession()) {
            tx = session.beginTransaction();
            session.merge(huella);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    /**
     * Elimina físicamente un registro de huella del sistema.
     * @param huella Objeto a eliminar.
     */
    public void eliminar(Huella huella) {
        Transaction tx = null;
        try (Session session = Connection.getInstance().openSession()) {
            tx = session.beginTransaction();
            session.remove(huella);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    /**
     * Recupera el historial detallado de un usuario específico.
     * Ordena los resultados por fecha descendente para mostrar primero
     * los registros más recientes.
     * @param idUsuario Identificador del usuario.
     * @return Lista de registros {@link Huella} con datos de actividad y categoría precargados.
     */
    public List<Huella> listarPorUsuario(int idUsuario) {
        try (Session session = Connection.getInstance().openSession()) {
            Query<Huella> query = session.createQuery(LISTAR_POR_USUARIO_COMPLETO, Huella.class);
            query.setParameter("idUsuario", idUsuario);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error al listar huellas: " + e.getMessage());
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    /**
     * Realiza el cálculo matemático de la huella de carbono total del usuario.
     * Delega la lógica de cálculo al motor de base de datos mediante una función
     * de agregado para optimizar el rendimiento.
     * @param idUsuario Identificador del usuario.
     * @return Suma total de CO2 equivalente o 0.0 si no existen registros.
     */
    public Double obtenerImpactoTotal(int idUsuario) {
        try (Session session = Connection.getInstance().openSession()) {
            Query<Double> query = session.createQuery(SUMA_IMPACTO_TOTAL, Double.class);
            query.setParameter("idUsuario", idUsuario);
            Double resultado = query.uniqueResult();
            return (resultado != null) ? resultado : 0.0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}