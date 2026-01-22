package services;

import DAO.HabitoDAO;
import model.Habito;
import java.util.List;

/**
 * Servicio de lógica de negocio para la gestión de hábitos sostenibles.
 * Coordina las operaciones de creación, eliminación y consulta de hábitos,
 * asegurando que los datos se procesen correctamente antes de enviarlos a la capa
 * de persistencia. Sirve como punto de control para las rutinas del usuario.
 */
public class HabitoService {

    /**
     * Instancia del DAO de hábitos para interactuar con la base de datos.
     */
    private final HabitoDAO habitoDAO;

    /**
     * Constructor que inicializa el servicio de hábitos.
     */
    public HabitoService() {
        this.habitoDAO = new HabitoDAO();
    }

    /**
     * Obtiene el listado de hábitos vinculados a un usuario.
     * @param idUsuario Identificador del usuario.
     * @return Lista de objetos {@link Habito} registrados por el usuario.
     */
    public List<Habito> misHabitos(int idUsuario) {
        return habitoDAO.listarPorUsuario(idUsuario);
    }

    /**
     * Registra o actualiza un hábito en el sistema.
     * Este método centraliza la llamada al DAO, permitiendo que el controlador
     * verifique si la operación fue exitosa mediante el valor de retorno.
     * @param h Instancia de {@link Habito} a guardar.
     * @return {@code true} si el hábito se guardó correctamente, {@code false} en caso contrario.
     */
    public boolean guardar(Habito h) {
        return habitoDAO.insertar(h);
    }

    /**
     * Elimina un hábito permanente del perfil del usuario.
     * @param h Objeto {@link Habito} que se desea borrar.
     */
    public void eliminar(Habito h) {
        habitoDAO.eliminar(h);
    }

    /**
     * Recupera los hábitos de un usuario utilizando una carga optimizada.
     * Este método es preferible para las vistas principales
     * ya que utiliza consultas con JOIN FETCH en el DAO, evitando cierres de sesión
     * inesperados al acceder a los detalles de la actividad.
     * @param userId Identificador único del usuario.
     * @return Lista de hábitos detallados.
     */
    public List<Habito> obtenerHabitosPorUsuario(int userId) {
        return habitoDAO.listarPorUsuario(userId);
    }
}