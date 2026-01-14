import controller.UsuarioController;
import connection.Connection;

public class MainTest {
    public static void main(String[] args) {
        // Instanciamos el controlador
        UsuarioController usuarioController = new UsuarioController();

        System.out.println("--- PROBANDO CONEXIÓN Y LOGIN ---");

        // Intento 1: Usuario correcto (el que insertamos en el SQL)
        // Ajusta el email y pass según lo que pusieras en tu script SQL
        String resultado1 = usuarioController.login("user1@example.com", "pass123");
        System.out.println("Prueba 1 (Correcta): " + resultado1);

        // Intento 2: Usuario incorrecto
        String resultado2 = usuarioController.login("mal@ejemplo.com", "1234");
        System.out.println("Prueba 2 (Incorrecta): " + resultado2);

        // Cerramos la factoría de sesiones al terminar
        Connection.getInstance().close();
        System.out.println("--- PRUEBA FINALIZADA ---");
    }
}
