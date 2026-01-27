package services;

import DAO.UsuarioDAO;
import model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Este servicio es el "guardián" de la aplicación.
 * Se encarga de todo lo que tenga que ver con los usuarios: desde crear cuentas nuevas
 * hasta asegurar que nadie entre si no tiene la llave (contraseña) correcta.
 */
public class UsuarioService {

    /**
     * Nuestra conexión directa con la base de datos para temas de usuarios.
     */
    private final UsuarioDAO usuarioDAO;

    /**
     * Al crear el servicio, preparamos su DAO para poder hablar con las tablas de MySQL.
     */
    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Gestiona el acceso al sistema.
     * Busca al usuario por su email y utiliza la librería BCrypt para verificar
     * si la contraseña introducida coincide con el hash de seguridad almacenado.
     * * @param email Correo electrónico del usuario.
     * @param password Contraseña en texto plano.
     * @return El objeto Usuario si las credenciales son válidas; null en caso contrario.
     */
    public Usuario login(String email, String password) {
        Usuario usuario = usuarioDAO.buscarPorEmail(email);

        // Comprobación de seguridad: comparamos el texto con el hash cifrado
        if (usuario != null && BCrypt.checkpw(password, usuario.getContrasena())) {
            return usuario;
        }
        return null;
    }

    /**
     * Registra un nuevo perfil en la base de datos.
     * Verifica que el email no esté duplicado y aplica un algoritmo de hashing
     * a la contraseña antes de guardarla para cumplir con los estándares de seguridad.
     * * @param nuevoUsuario Objeto con los datos del perfil a registrar.
     * @return true si el registro fue exitoso; false si el email ya existe.
     */
    public boolean registrarUsuario(Usuario nuevoUsuario) {
        if (usuarioDAO.buscarPorEmail(nuevoUsuario.getEmail()) == null) {

            // Generamos un hash seguro con sal aleatoria (salt) para la contraseña
            String hash = BCrypt.hashpw(nuevoUsuario.getContrasena(), BCrypt.gensalt());
            nuevoUsuario.setContrasena(hash);

            usuarioDAO.guardar(nuevoUsuario);
            return true;
        }
        return false;
    }
}