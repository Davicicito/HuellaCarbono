package DAO;

import model.Habito;
import model.Recomendacion;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import utils.HibernateUtil; // Usaremos este siempre para evitar el error de conexión cerrada

import java.util.List;

public class HabitoDAO {

    // --- CONSULTAS HQL ---
    private static final String BUSCAR_POR_USUARIO = "FROM Habito WHERE idUsuario.id = :idUsuario";

    private static final String RECOMENDACIONES_POR_HABITO =
            "SELECT r FROM Recomendacion r " +
                    "WHERE r.idCategoria.id IN (" +
                    "   SELECT h.idActividad.idCategoria.id FROM Habito h WHERE h.idUsuario.id = :idUsuario" +
                    ")";

    private static final String LISTAR_COMPLETO =
            "SELECT h FROM Habito h " +
                    "JOIN FETCH h.idActividad a " +
                    "JOIN FETCH a.idCategoria " +
                    "WHERE h.idUsuario.id = :idUsuario";

    // --- MÉTODOS CRUD ---

    /**
     * Guarda un hábito nuevo. Retorna true si tuvo éxito.
     */
    public boolean insertar(Habito h) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // CAMBIO CLAVE: Usamos merge() en lugar de persist()
            // merge() reasocia las entidades 'detached' (Usuario y Actividad) a la sesión actual
            session.merge(h);

            transaction.commit();
            System.out.println("Hábito guardado correctamente con merge.");
            return true;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Error en HabitoDAO: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Este método hace lo mismo que insertar, lo mantenemos por compatibilidad
    public void guardar(Habito habito) {
        insertar(habito);
    }

    public void actualizar(Habito habito) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(habito);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    public void eliminar(Habito habito) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            // Nos aseguramos de que el objeto esté gestionado antes de borrar
            session.remove(session.contains(habito) ? habito : session.merge(habito));
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    public List<Habito> listarPorUsuario(int idUsuario) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(LISTAR_COMPLETO, Habito.class)
                    .setParameter("idUsuario", idUsuario)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // --- CONSULTAS ADICIONALES ---

    public List<Recomendacion> obtenerRecomendacionesSugeridas(int idUsuario) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Recomendacion> query = session.createQuery(RECOMENDACIONES_POR_HABITO, Recomendacion.class);
            query.setParameter("idUsuario", idUsuario);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}