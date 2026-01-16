package utils;

import model.Usuario;

public class Sesion {
    private static Sesion instancia;
    private Usuario usuarioLogueado;

    private Sesion() {}

    public static Sesion getInstancia() {
        if (instancia == null) {
            instancia = new Sesion();
        }
        return instancia;
    }

    public Usuario getUsuario() { return usuarioLogueado; }
    public void setUsuario(Usuario u) { this.usuarioLogueado = u; }
}