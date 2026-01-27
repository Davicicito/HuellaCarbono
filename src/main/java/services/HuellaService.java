package services;

import DAO.HuellaDAO;
import model.Huella;
import org.hibernate.Session;
import utils.HibernateUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio encargado de la lógica de negocio para la gestión de huellas de carbono.
 * Proporciona métodos para el registro, eliminación y cálculo avanzado de métricas
 * de impacto ambiental, centralizando las consultas HQL para el análisis de datos.
 */
public class HuellaService {

    // --- CONSULTAS HQL ---

    private static final String QUERY_HISTORIAL =
            "SELECT h FROM Huella h " +
                    "JOIN FETCH h.idActividad a " +
                    "JOIN FETCH a.idCategoria " +
                    "WHERE h.idUsuario.id = :id " +
                    "ORDER BY h.fecha DESC";

    private static final String QUERY_SUM_IMPACTO =
            "SELECT SUM(h.valor * h.idActividad.idCategoria.factorEmision) " +
                    "FROM Huella h WHERE h.idUsuario.id = :id";

    private static final String QUERY_COUNT_ACTIVIDADES =
            "SELECT COUNT(h) FROM Huella h WHERE h.idUsuario.id = :id";

    private final HuellaDAO huellaDAO;

    public HuellaService() {
        this.huellaDAO = new HuellaDAO();
    }

    /**
     * Registra una nueva actividad en el sistema delegando la persistencia al DAO.
     * @param h Instancia de Huella con los datos de consumo.
     */
    public void registrarNuevaHuella(Huella h) {
        huellaDAO.guardar(h);
    }

    /**
     * Recupera el historial de huellas del usuario.
     * Utiliza JOIN FETCH para obtener la actividad y categoría en una sola consulta,
     * evitando problemas de carga perezosa (Lazy Initialization) en la interfaz.
     * @param idUsuario Identificador del usuario.
     * @return Lista de huellas ordenadas por fecha de forma descendente.
     */
    public List<Huella> obtenerHistorial(int idUsuario) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(QUERY_HISTORIAL, Huella.class)
                    .setParameter("id", idUsuario)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    /**
     * Elimina un registro de huella de la base de datos.
     * @param h Entidad Huella a eliminar.
     */
    public void borrarRegistro(Huella h) {
        huellaDAO.eliminar(h);
    }

    /**
     * Calcula las estadísticas principales para el inicio.
     * Obtiene el impacto total multiplicado por los factores de emisión y el
     * recuento total de actividades registradas por el usuario.
     * @param usuarioId Identificador del usuario.
     * @return Mapa con el impacto acumulado ("total") y el número de registros ("conteo").
     */
    public Map<String, Double> obtenerEstadisticas(long usuarioId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Map<String, Double> stats = new HashMap<>();

            // Cálculo del impacto total (Valor * Factor de Emisión)
            Double totalImpacto = session.createQuery(QUERY_SUM_IMPACTO, Double.class)
                    .setParameter("id", (int)usuarioId)
                    .getSingleResult();

            stats.put("total", totalImpacto != null ? totalImpacto : 0.0);

            // Recuento de actividades
            Long actividades = session.createQuery(QUERY_COUNT_ACTIVIDADES, Long.class)
                    .setParameter("id", (int)usuarioId)
                    .getSingleResult();

            stats.put("conteo", actividades != null ? actividades.doubleValue() : 0.0);

            return stats;
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Double> errorStats = new HashMap<>();
            errorStats.put("total", 0.0);
            errorStats.put("conteo", 0.0);
            return errorStats;
        }
    }

    /**
     * Consulta el impacto total acumulado utilizando la lógica aritmética del DAO.
     * @param idUsuario Identificador del usuario.
     * @return Suma total del impacto en kg de CO2.
     */
    public double consultarImpactoTotal(int idUsuario) {
        return huellaDAO.obtenerImpactoTotal(idUsuario);
    }

    /**
     * Obtiene la lista de huellas del usuario mediante la consulta optimizada del DAO.
     * @param idUsuario Identificador del usuario.
     * @return Lista de entidades Huella.
     */
    public List<Huella> obtenerHuellasPorUsuario(int idUsuario) {
        return huellaDAO.listarPorUsuario(idUsuario);
    }
}