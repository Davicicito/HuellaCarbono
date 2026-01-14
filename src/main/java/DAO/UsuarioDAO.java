package DAO;

import connection.Connection;
import model.Usuario;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class UsuarioDAO {

    // --- CONSULTAS HQL ---
    private static final String BUSCAR_POR_EMAIL = "FROM Usuario WHERE email = :email";

    // --- MÉTODOS ---

    /**
     * Busca un usuario por su correo electrónico.
     * Útil para el proceso de inicio de sesión.
     */
    public Usuario buscarPorEmail(String email) {
        try (Session session = Connection.getInstance().openSession()) {
            Query<Usuario> query = session.createQuery(BUSCAR_POR_EMAIL, Usuario.class);
            query.setParameter("email", email);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Guarda un nuevo usuario en la base de datos[cite: 9, 83].
     */
    public void guardar(Usuario usuario) {
        Transaction transaction = null;
        try (Session session = Connection.getInstance().openSession()) {
            transaction = session.beginTransaction();
            session.persist(usuario);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
}