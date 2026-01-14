package DAO;

import connection.Connection;
import model.Actividad;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import java.util.List;

public class ActividadDAO {

    // --- CONSULTAS HQL ---
    private static final String LISTAR_TODAS = "FROM Actividad";
    private static final String BUSCAR_POR_CATEGORIA = "FROM Actividad WHERE idCategoria.id = :idCat";

    // --- MÉTODOS ---

    /**
     * Recupera todas las actividades de la BBDD para llenar los combos de la interfaz.
     */
    public List<Actividad> listarTodas() {
        try (Session session = Connection.getInstance().openSession()) {
            Query<Actividad> query = session.createQuery(LISTAR_TODAS, Actividad.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Busca actividades de una categoría específica (ej: solo Transporte). [cite: 17]
     */
    public List<Actividad> listarPorCategoria(int idCategoria) {
        try (Session session = Connection.getInstance().openSession()) {
            Query<Actividad> query = session.createQuery(BUSCAR_POR_CATEGORIA, Actividad.class);
            query.setParameter("idCat", idCategoria);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
