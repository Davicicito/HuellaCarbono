package DAO;

import connection.Connection;
import model.Habito;
import model.Recomendacion;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class HabitoDAO {

    // --- CONSULTAS HQL ---
    private static final String BUSCAR_POR_USUARIO = "FROM Habito WHERE idUsuario.id = :idUsuario";

    // Consulta que busca recomendaciones filtrando por las categorías de los hábitos del usuario [cite: 21, 22]
    private static final String RECOMENDACIONES_POR_HABITO =
            "SELECT r FROM Recomendacion r " +
                    "WHERE r.idCategoria.id IN (" +
                    "   SELECT h.idActividad.idCategoria.id FROM Habito h WHERE h.idUsuario.id = :idUsuario" +
                    ")";

    // --- MÉTODOS CRUD [cite: 83] ---

    public void guardar(Habito habito) {
        Session session = Connection.getInstance().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.persist(habito);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback(); // Verificamos que la tx esté activa
            e.printStackTrace();
        } finally {
            session.close(); // Cerramos siempre al final
        }
    }

    public void actualizar(Habito habito) {
        Transaction tx = null;
        try (Session session = Connection.getInstance().openSession()) {
            tx = session.beginTransaction();
            session.merge(habito);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public void eliminar(Habito habito) {
        Transaction tx = null;
        try (Session session = Connection.getInstance().openSession()) {
            tx = session.beginTransaction();
            session.remove(habito);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }

    public List<Habito> listarPorUsuario(int idUsuario) {
        try (Session session = Connection.getInstance().openSession()) { //
            return session.createQuery(
                            "SELECT h FROM Habito h " +
                                    "JOIN FETCH h.idActividad a " +
                                    "JOIN FETCH a.idCategoria " +
                                    "WHERE h.idUsuario.id = :idUsuario", Habito.class)
                    .setParameter("idUsuario", idUsuario)
                    .getResultList();
        }
    }

    // --- CONSULTA ADICIONAL ---
    public List<Recomendacion> obtenerRecomendacionesSugeridas(int idUsuario) {
        try (Session session = Connection.getInstance().openSession()) {
            Query<Recomendacion> query = session.createQuery(RECOMENDACIONES_POR_HABITO, Recomendacion.class);
            query.setParameter("idUsuario", idUsuario);
            return query.getResultList();
        }
    }
}