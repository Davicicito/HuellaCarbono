package DAO;

import connection.Connection;
import model.Recomendacion;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;

public class RecomendacionDAO {

    private static final String LISTAR_POR_CATEGORIA = "FROM Recomendacion WHERE idCategoria.id = :idCat";

    public List<Recomendacion> listarPorCategoria(int idCategoria) {
        try (Session session = Connection.getInstance().openSession()) {
            Query<Recomendacion> query = session.createQuery(LISTAR_POR_CATEGORIA, Recomendacion.class);
            query.setParameter("idCat", idCategoria);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
