package connection;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Clase encargada de gestionar la conexión con la base de datos mediante Hibernate.
 * Implementa el patrón Singleton para asegurar que solo exista una factoría de
 * sesiones en toda la aplicación, optimizando así el uso de recursos.
 */
public class Connection {

    /**
     * Única instancia de la clase Connection.
     */
    private static Connection instance;

    /**
     * Factoría de sesiones de Hibernate para gestionar el ciclo de vida de las conexiones.
     */
    private SessionFactory sessionFactory;

    /**
     * Constructor privado que inicializa la configuración de Hibernate.
     * Lee el archivo hibernate.cfg.xml para levantar la SessionFactory.
     * * @throws RuntimeException Si hay un fallo crítico al configurar la base de datos.
     */
    private Connection() {
        try {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al construir la SessionFactory");
        }
    }

    /**
     * Obtiene la instancia única de esta clase. Si no existe, la crea.
     * Es el punto de acceso global para obtener conexiones.
     * * @return La instancia activa de Connection.
     */
    public static Connection getInstance() {
        if (instance == null) {
            instance = new Connection();
        }
        return instance;
    }

    /**
     * Abre y devuelve una nueva sesión de Hibernate.
     * Debe usarse cada vez que necesitemos interactuar con los DAO.
     * * @return Una instancia de {@link Session} lista para operar.
     */
    public Session openSession() {
        return sessionFactory.openSession();
    }

}