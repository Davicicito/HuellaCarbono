package services;

import DAO.UsuarioDAO;
import model.Usuario;

/**
 * Servicio encargado de la gestión de usuarios y seguridad del sistema.
 * Centraliza los procesos de autenticación y registro de nuevos perfiles,
 * aplicando las reglas de negocio necesarias para garantizar la integridad
 * de las cuentas de usuario en EcoTrack.
 */
public class UsuarioService {

    /**
     * Acceso a la capa de persistencia para la gestión de usuarios.
     */
    private final UsuarioDAO usuarioDAO;

    /**
     * Constructor que inicializa el servicio y su correspondiente DAO.
     */
    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Procesa la validación de credenciales para el acceso al sistema.
     * Recupera el perfil mediante el email y realiza una comparación segura
     * de la contraseña almacenada.
     * * @param email Correo electrónico proporcionado por el usuario.
     * @param password Contraseña introducida en el formulario de acceso.
     * @return El objeto {@link Usuario} si la autenticación es exitosa;
     * {@code null} si el usuario no existe o las credenciales son incorrectas.
     */
    public Usuario login(String email, String password) {
        Usuario usuario = usuarioDAO.buscarPorEmail(email);

        // Verificación de identidad: coincidencia exacta de credenciales
        if (usuario != null && usuario.getContrasena().equals(password)) {
            return usuario;
        }
        return null;
    }

    /**
     * Gestiona el registro de un nuevo usuario en la plataforma.
     * Implementa la regla de negocio de "email único", verificando que la cuenta
     * no exista previamente antes de proceder con el guardado en la base de datos.
     * * @param nuevoUsuario Instancia con los datos del perfil a crear.
     * @return {@code true} si el registro fue exitoso;
     * {@code false} si el correo electrónico ya se encuentra registrado.
     */
    public boolean registrarUsuario(Usuario nuevoUsuario) {
        // Validación de unicidad: evitamos duplicados en la base de datos
        if (usuarioDAO.buscarPorEmail(nuevoUsuario.getEmail()) == null) {
            usuarioDAO.guardar(nuevoUsuario);
            return true;
        }
        return false;
    }
}