package services;

import DAO.HuellaDAO;
import model.Huella;
import org.hibernate.Session;
import utils.HibernateUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HuellaService {
    private final HuellaDAO huellaDAO;

    public HuellaService() {
        this.huellaDAO = new HuellaDAO();
    }

    /**
     * Registra una nueva huella en la base de datos.
     */
    public void registrarNuevaHuella(Huella h) {
        huellaDAO.guardar(h);
    }

    /**
     * Obtiene todos los registros de huella de un usuario específico.
     */
    public List<Huella> obtenerHistorial(int idUsuario) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // El JOIN FETCH es la clave para evitar el LazyInitializationException
            return session.createQuery(
                            "SELECT h FROM Huella h JOIN FETCH h.idActividad WHERE h.idUsuario.id = :id ORDER BY h.fecha DESC",
                            Huella.class)
                    .setParameter("id", idUsuario)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    /**
     * Borra un registro de huella.
     */
    public void borrarRegistro(Huella h) {
        huellaDAO.eliminar(h);
    }

    /**
     * LÓGICA PARA EL DASHBOARD:
     * Calcula el impacto total y el número de actividades para el usuario logueado.
     */
    public Map<String, Double> obtenerEstadisticas(long usuarioId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Map<String, Double> stats = new HashMap<>();

            // 1. Suma total de CO2 usando tu campo 'valor'
            // Filtramos por idUsuario.id porque en tu modelo Huella el campo se llama idUsuario
            Double total = session.createQuery(
                            "SELECT SUM(h.valor) FROM Huella h WHERE h.idUsuario.id = :id", Double.class)
                    .setParameter("id", (int)usuarioId)
                    .getSingleResult();

            stats.put("total", total != null ? total.doubleValue() : 0.0);

            // 2. Conteo total de registros (actividades)
            Long actividades = session.createQuery(
                            "SELECT COUNT(h) FROM Huella h WHERE h.idUsuario.id = :id", Long.class)
                    .setParameter("id", (int)usuarioId)
                    .getSingleResult();

            stats.put("conteo", actividades != null ? actividades.doubleValue() : 0.0);

            return stats;
        } catch (Exception e) {
            e.printStackTrace();
            // Si hay error, devolvemos valores en 0 para que la app no explote
            Map<String, Double> errorStats = new HashMap<>();
            errorStats.put("total", 0.0);
            errorStats.put("conteo", 0.0);
            return errorStats;
        }
    }

    /**
     * Consulta el impacto total (otra forma de obtenerlo si lo necesitas solo).
     */
    public double consultarImpactoTotal(int idUsuario) {
        return huellaDAO.obtenerImpactoTotal(idUsuario);
    }
}