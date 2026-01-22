package DAO;

import model.Habito;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import utils.HibernateUtil;
import java.util.List;

/**
 * Clase de acceso a datos (DAO) para la gestión de hábitos sostenibles.
 * Esta clase implementa la lógica de persistencia para los compromisos de usuario,
 * utilizando estrategias de carga optimizada (Eager Loading) para evitar errores
 * de inicialización perezosa al navegar por la jerarquía de Actividades y Categorías.
 */
public class HabitoDAO {

    /**
     * Consulta HQL optimizada mediante JOIN FETCH.
     * Recupera los hábitos de un usuario cargando en una sola operación los objetos
     * relacionados de Actividad y Categoría, eliminando el problema de las N+1 consultas.
     */
    private static final String LISTAR_COMPLETO =
            "SELECT h FROM Habito h " +
                    "JOIN FETCH h.idActividad a " +
                    "JOIN FETCH a.idCategoria " +
                    "WHERE h.idUsuario.id = :idUsuario";

    /**
     * Inserta o actualiza un hábito en la base de datos.
     * Utiliza el método {@code merge()} para reasociar entidades que pueden estar
     * en estado 'detached' (desconectadas), asegurando que las relaciones con
     * Usuario y Actividad se mantengan íntegras.
     * * @param h El objeto {@link Habito} a persistir.
     * @return {@code true} si la transacción se completó con éxito, {@code false} en caso de error.
     */
    public boolean insertar(Habito h) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Sincronización del estado del objeto con la sesión actual
            session.merge(h);

            transaction.commit();
            System.out.println("Hábito guardado correctamente.");
            return true;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Error en HabitoDAO al insertar: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina un registro de hábito de la base de datos.
     * Realiza una comprobación previa para verificar si el objeto está gestionado
     * por la sesión actual; si no lo está, lo reasocia antes de proceder al borrado físico.
     * * @param habito El objeto {@link Habito} que se desea eliminar.
     */
    public void eliminar(Habito habito) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            // Verificación del estado de gestión del objeto para evitar excepciones de Hibernate
            session.remove(session.contains(habito) ? habito : session.merge(habito));
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            System.err.println("Error al eliminar hábito: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Recupera el listado detallado de hábitos de un usuario específico.
     * Gracias al uso de la constante {@code LISTAR_COMPLETO}, los objetos devueltos
     * contienen toda su información relacionada lista para ser mostrada en la interfaz.
     * * @param idUsuario Identificador del usuario.
     * @return Una lista de {@link Habito} asociados al usuario.
     */
    public List<Habito> listarPorUsuario(int idUsuario) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(LISTAR_COMPLETO, Habito.class)
                    .setParameter("idUsuario", idUsuario)
                    .getResultList();
        } catch (Exception e) {
            System.err.println("Error al listar hábitos por usuario: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
}