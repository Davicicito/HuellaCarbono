package services;

import DAO.UsuarioDAO;
import model.Usuario;

public class UsuarioService {
    private final UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Lógica para validar el acceso al sistema.
     * @return El objeto Usuario si las credenciales son correctas, null en caso contrario.
     */
    public Usuario login(String email, String password) {
        Usuario usuario = usuarioDAO.buscarPorEmail(email);

        // Verificamos si existe el usuario y si la contraseña coincide con la de la BBDD [cite: 29]
        if (usuario != null && usuario.getContrasena().equals(password)) {
            return usuario;
        }
        return null;
    }

    /**
     * Lógica para registrar un nuevo perfil.
     */
    public boolean registrarUsuario(Usuario nuevoUsuario) {
        // Podríamos añadir validaciones extra aquí (ej: si el email ya existe)
        if (usuarioDAO.buscarPorEmail(nuevoUsuario.getEmail()) == null) {
            usuarioDAO.guardar(nuevoUsuario);
            return true;
        }
        return false;
    }
}