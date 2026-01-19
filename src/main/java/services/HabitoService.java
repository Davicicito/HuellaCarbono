package services;

import DAO.HabitoDAO;
import model.Habito;
import model.Recomendacion;
import java.util.List;

public class HabitoService {
    private final HabitoDAO habitoDAO;

    public HabitoService() {
        this.habitoDAO = new HabitoDAO();
    }

    public void asignarHabito(Habito h) {
        habitoDAO.guardar(h); // [cite: 15, 83]
    }

    public List<Recomendacion> sugerirMejoras(int idUsuario) {
        // Lógica para obtener las recomendaciones basadas en los hábitos [cite: 16, 22]
        return habitoDAO.obtenerRecomendacionesSugeridas(idUsuario);
    }

    public List<Habito> misHabitos(int idUsuario) {
        return habitoDAO.listarPorUsuario(idUsuario);
    }

    /**
     * Guarda un nuevo hábito en la base de datos a través del DAO.
     * Este es el método que tu controlador marca en rojo.
     */
    public boolean guardar(Habito h) {
        // Llamamos al método insertar del DAO que ahora devuelve boolean
        return habitoDAO.insertar(h);
    }

    public void eliminar(Habito h) {
        habitoDAO.eliminar(h);
    }
}
