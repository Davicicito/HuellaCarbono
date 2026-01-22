package utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Clase de utilidad para la configuración y gestión de Hibernate.
 * Implementa el patrón Singleton para garantizar que solo exista una instancia
 * de {@link SessionFactory} en toda la aplicación, optimizando el uso de recursos
 * y la gestión del pool de conexiones a la base de datos.
 */
public class HibernateUtil {

    /**
     * Instancia única de SessionFactory, inicializada al cargar la clase.
     */
    private static final SessionFactory sessionFactory = buildSessionFactory();

    /**
     * Construye la factoría de sesiones a partir de la configuración XML.
     * Lee el archivo {@code hibernate.cfg.xml} para establecer los parámetros
     * de conexión, el dialecto SQL y el mapeo de entidades.
     * * @return Una instancia configurada de {@link SessionFactory}.
     * @throws ExceptionInInitializerError Si ocurre un fallo crítico durante la
     * configuración.
     */
    private static SessionFactory buildSessionFactory() {
        try {
            // Crea la SessionFactory a partir del archivo hibernate.cfg.xml por defecto
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Error fatal: No se pudo crear la SessionFactory inicial. " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Proporciona acceso global a la factoría de sesiones de Hibernate.
     * Este es el punto de entrada para que los DAOs puedan abrir nuevas sesiones
     * y realizar transacciones.
     * * @return La instancia única de {@link SessionFactory}.
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Finaliza la infraestructura de Hibernate de forma segura.
     * Cierra los pools de conexiones y libera los recursos de caché. Debe
     * invocarse al cerrar la aplicación para evitar fugas de memoria o
     * conexiones huérfanas en el servidor de base de datos.
     */
    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            getSessionFactory().close();
        }
    }
}