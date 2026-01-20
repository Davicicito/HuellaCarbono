package DAO;

import connection.Connection;
import model.Huella;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class HuellaDAO {

    // --- CONSULTAS HQL ---

    // Usamos JOIN FETCH para cargar Actividad y Categoría de golpe y evitar errores en los gráficos
    private static final String LISTAR_POR_USUARIO_COMPLETO =
            "SELECT h FROM Huella h " +
                    "JOIN FETCH h.idActividad a " +
                    "JOIN FETCH a.idCategoria " +
                    "WHERE h.idUsuario.id = :idUsuario " +
                    "ORDER BY h.fecha DESC";

    private static final String SUMA_IMPACTO_TOTAL =
            "SELECT SUM(h.valor * h.idActividad.idCategoria.factorEmision) " +
                    "FROM Huella h WHERE h.idUsuario.id = :idUsuario";

    // --- MÉTODOS CRUD ---

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
     * Obtiene todas las huellas de un usuario.
     * Es vital usar esta versión para la pestaña de Análisis.
     */
    public List<Huella> listarPorUsuario(int idUsuario) {
        try (Session session = Connection.getInstance().openSession()) {
            // Asegúrate de que LISTAR_POR_USUARIO_COMPLETO esté definido arriba
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
     * Calcula la suma total del impacto (CO2) del usuario.
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