package DAO;

import connection.Connection;
import model.Categoria;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;

public class CategoriaDAO {

    private static final String LISTAR_TODAS = "FROM Categoria";

    public List<Categoria> listarTodas() {
        try (Session session = Connection.getInstance().openSession()) {
            Query<Categoria> query = session.createQuery(LISTAR_TODAS, Categoria.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
