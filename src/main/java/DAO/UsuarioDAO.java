package DAO;

import connection.Connection;
import model.Usuario;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

/**
 * Clase de acceso a datos (DAO) para la entidad Usuario.
 * Proporciona los mecanismos necesarios para la gestión de cuentas de usuario,
 * permitiendo la recuperación de perfiles para la autenticación y el registro
 * de nuevos integrantes en el sistema EcoTrack.
 */
public class UsuarioDAO {

    /**
     * Consulta HQL para localizar un usuario a través de su correo electrónico,
     * el cual actúa como identificador único en la lógica de negocio.
     */
    private static final String BUSCAR_POR_EMAIL = "FROM Usuario WHERE email = :email";

    /**
     * Localiza un registro de usuario en la base de datos utilizando su email.
     * Este método es el pilar del sistema de login, permitiendo verificar
     * la existencia de una cuenta antes de validar sus credenciales.
     * * @param email Dirección de correo electrónico a consultar.
     * @return El objeto {@link Usuario} correspondiente o {@code null} si no se encuentra.
     */
    public Usuario buscarPorEmail(String email) {
        try (Session session = Connection.getInstance().openSession()) {
            Query<Usuario> query = session.createQuery(BUSCAR_POR_EMAIL, Usuario.class);
            query.setParameter("email", email);
            // uniqueResult devuelve un solo objeto o lanza excepción si hay duplicados
            return query.uniqueResult();
        } catch (Exception e) {
            System.err.println("Error al buscar usuario por email: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Almacena de forma permanente un nuevo perfil de usuario en el sistema.
     * Gestiona la transacción de Hibernate de principio a fin, asegurando que
     * el proceso se revierta (rollback) íntegramente en caso de fallo crítico.
     * * @param usuario Instancia de {@link Usuario} con los datos de registro.
     */
    public void guardar(Usuario usuario) {
        Transaction transaction = null;
        try (Session session = Connection.getInstance().openSession()) {
            transaction = session.beginTransaction();
            // persist transfiere el objeto al estado 'managed' dentro del contexto de Hibernate
            session.persist(usuario);
            transaction.commit();
            System.out.println("Usuario registrado correctamente en la base de datos.");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error al persistir nuevo usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }
}