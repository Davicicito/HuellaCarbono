package DAO;

import connection.Connection;
import model.Huella;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class HuellaDAO {

    // --- CONSULTAS HQL ---
    private static final String BUSCAR_POR_USUARIO = "FROM Huella WHERE idUsuario.id = :idUsuario";

    // HQL para calcular el impacto total del usuario basándose en categorías [cite: 17, 18]
    private static final String SUMA_IMPACTO_TOTAL =
            "SELECT SUM(h.valor * h.idActividad.idCategoria.factorEmision) " +
                    "FROM Huella h WHERE h.idUsuario.id = :idUsuario";

    // --- MÉTODOS CRUD [cite: 83] ---

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

    public List<Huella> listarPorUsuario(int idUsuario) {
        try (Session session = Connection.getInstance().openSession()) {
            Query<Huella> query = session.createQuery(BUSCAR_POR_USUARIO, Huella.class);
            query.setParameter("idUsuario", idUsuario);
            return query.getResultList();
        }
    }

    // --- CONSULTA COMPLEJA ---
    public Double obtenerImpactoTotal(int idUsuario) {
        try (Session session = Connection.getInstance().openSession()) {
            Query<Double> query = session.createQuery(SUMA_IMPACTO_TOTAL, Double.class);
            query.setParameter("idUsuario", idUsuario);
            Double resultado = query.uniqueResult();
            return (resultado != null) ? resultado : 0.0;
        }
    }
}