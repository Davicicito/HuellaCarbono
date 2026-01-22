//import controller.*;
//import model.*;
//import connection.Connection;
//import java.time.LocalDate;
//import java.util.List;
//
//public class MainTest {
//    public static void main(String[] args) {
//        // 1. Inicializamos los controladores necesarios
//        UsuarioController usuarioCont = new UsuarioController();
//        HuellaController huellaCont = new HuellaController();
//        ActividadController actividadCont = new ActividadController();
//        HabitoController habitoCont = new HabitoController();
//
//        System.out.println("=== INICIANDO PRUEBA INTEGRAL DEL SISTEMA ===");
//
//        // 2. Simulación de Login [cite: 9, 83]
//        // Usamos los datos que insertamos previamente en el SQL
//        Usuario usuarioActual = usuarioCont.login("user1@example.com", "pass123");
//
//        if (usuarioActual != null) {
//            System.out.println("1. Login exitoso: Bienvenido " + usuarioActual.getNombre());
//
//            // 3. Listar actividades disponibles (Leídas de la BBDD)
//            List<Actividad> actividades = actividadCont.listarActividades();
//            System.out.println("2. Actividades disponibles en el sistema: " + actividades.size());
//
//            // Supongamos que el usuario elige la primera actividad: "Conducir coche"
//            Actividad conducirCoche = actividades.get(0);
//
//            // 4. Registrar una nueva Huella [cite: 9, 10, 11, 12, 13]
//            Huella nuevaHuella = new Huella();
//            nuevaHuella.setIdUsuario(usuarioActual);
//            nuevaHuella.setIdActividad(conducirCoche);
//            nuevaHuella.setValor(150.0f); // Ejemplo: 150 km [cite: 10, 69]
//            nuevaHuella.setUnidad("km");
//            nuevaHuella.setFecha(LocalDate.now());
//
//            huellaCont.guardarHuella(nuevaHuella);
//            System.out.println("3. Huella registrada: 150 km de 'Conducir coche'.");
//
//            // 5. Calcular Impacto Ambiental (Fórmula: Valor * Factor)
//            double impacto = huellaCont.getImpactoTotal(usuarioActual.getId());
//            System.out.println("4. Impacto Total calculado (HQL): " + impacto + " kg de CO2");
//
//            // 6. Registrar un Hábito (Actividad frecuente) [cite: 15, 76]
//            // Usamos la clave compuesta HabitoId
//            HabitoId idHabito = new HabitoId();
//            idHabito.setIdUsuario(usuarioActual.getId());
//            idHabito.setIdActividad(conducirCoche.getId());
//
//            Habito nuevoHabito = new Habito();
//            nuevoHabito.setId(idHabito);
//            nuevoHabito.setIdUsuario(usuarioActual);
//            nuevoHabito.setIdActividad(conducirCoche);
//            nuevoHabito.setFrecuencia(3); // 3 veces por semana [cite: 76]
//            nuevoHabito.setTipo("semanal");
//            nuevoHabito.setUltimaFecha(LocalDate.now());
//
//            habitoCont.registrarHabito(nuevoHabito);
//            System.out.println("5. Hábito registrado: Conducir coche 3 veces/semana.");
//
//            // 7. Obtener Recomendaciones Personalizadas según hábitos [cite: 16, 21, 22]
//            List<Recomendacion> recomendaciones = habitoCont.getSugerencias(usuarioActual.getId());
//            System.out.println("6. Recomendaciones para mejorar tu huella:");
//            for (Recomendacion r : recomendaciones) {
//                System.out.println("   - " + r.getDescripcion() + " (Ahorro est.: " + r.getImpactoEstimado() + " kg)");
//            }
//
//        } else {
//            System.out.println("Error: No se pudo iniciar sesión. Revisa los datos de la BBDD.");
//        }
//
//        // 8. Cerramos la conexión al terminar
//        Connection.getInstance().close();
//        System.out.println("=== PRUEBA FINALIZADA ===");
//    }
//}