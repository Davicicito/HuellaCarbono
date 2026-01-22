package utils;

import model.Usuario;

/**
 * Clase de utilidad que gestiona el estado de la sesión actual del usuario.
 * Implementa el patrón de diseño Singleton para garantizar que exista
 * una única instancia de la sesión en toda la ejecución de la aplicación,
 * permitiendo que todos los controladores accedan a la información del
 * usuario autenticado de forma centralizada.
 */
public class Sesion {

    /**
     * Instancia única de la clase Sesion (Singleton).
     */
    private static Sesion instancia;

    /**
     * Referencia al objeto Usuario que ha iniciado sesión correctamente.
     */
    private Usuario usuarioLogueado;

    /**
     * Constructor privado para evitar la instanciación externa de la clase,
     * cumpliendo con los requisitos del patrón Singleton.
     */
    private Sesion() {}

    /**
     * Proporciona acceso a la instancia única de la sesión.
     * Si la instancia aún no existe, la crea (Lazy Initialization);
     * de lo contrario, devuelve la existente.
     * * @return La instancia única de {@link Sesion}.
     */
    public static Sesion getInstancia() {
        if (instancia == null) {
            instancia = new Sesion();
        }
        return instancia;
    }

    /**
     * Obtiene el perfil del usuario que se encuentra actualmente en el sistema.
     * * @return El objeto {@link Usuario} logueado, o {@code null} si no hay
     * ninguna sesión activa.
     */
    public Usuario getUsuario() {
        return usuarioLogueado;
    }

    /**
     * Establece el usuario que ha superado el proceso de autenticación.
     * Este método es invocado por el controlador de login al iniciar sesión
     * y por el sistema de logout para limpiar la sesión (asignando null).
     * * @param u El objeto {@link Usuario} que representará la sesión activa.
     */
    public void setUsuario(Usuario u) {
        this.usuarioLogueado = u;
    }
}