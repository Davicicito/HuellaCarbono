package controller;

import model.Usuario;
import services.UsuarioService;

public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController() {
        this.usuarioService = new UsuarioService();
    }

    public String login(String email, String password) {
        Usuario usuario = usuarioService.login(email, password);
        if (usuario != null) {
            return "¡Bienvenido, " + usuario.getNombre() + "! Inicio de sesión correcto.";
        } else {
            return "Error: Credenciales incorrectas.";
        }
    }
}
