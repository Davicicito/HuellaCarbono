package controller;

import model.Usuario;
import services.UsuarioService;

public class UsuarioController {
    private final UsuarioService usuarioService = new UsuarioService();

    public Usuario login(String email, String password) {
        return usuarioService.login(email, password);
    }

    public boolean registrar(Usuario u) {
        return usuarioService.registrarUsuario(u);
    }
}