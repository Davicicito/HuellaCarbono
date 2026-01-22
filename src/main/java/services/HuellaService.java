package services;

import DAO.HuellaDAO;
import model.Huella;
import org.hibernate.Session;
import utils.HibernateUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio central para la gestión de la huella de carbono.
 * Proporciona la lógica de negocio para registrar, eliminar y consultar el impacto
 * ambiental de los usuarios. Incluye métodos avanzados de agregación para generar
 * las estadísticas y métricas que se visualizan en el Dashboard principal.
 */
public class HuellaService {

    /**
     * Acceso a la persistencia especializada en registros de huella.
     */
    private final HuellaDAO huellaDAO;

    /**
     * Constructor que inicializa el servicio de huellas.
     */
    public HuellaService() {
        this.huellaDAO = new HuellaDAO();
    }

    /**
     * Registra una nueva entrada de actividad ambiental en el sistema.
     * @param h Objeto {@link Huella} que contiene el valor, unidad y actividad realizada.
     */
    public void registrarNuevaHuella(Huella h) {
        huellaDAO.guardar(h);
    }

    /**
     * Obtiene el historial cronológico de huellas de un usuario.
     * Utiliza una consulta HQL con JOIN FETCH para cargar en una única operación
     * la actividad y su categoría asociada, optimizando el rendimiento de la UI.
     * @param idUsuario Identificador del usuario logueado.
     * @return Lista de huellas ordenadas de más reciente a más antigua.
     */
    public List<Huella> obtenerHistorial(int idUsuario) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT h FROM Huella h " +
                                    "JOIN FETCH h.idActividad a " +
                                    "JOIN FETCH a.idCategoria " +
                                    "WHERE h.idUsuario.id = :id " +
                                    "ORDER BY h.fecha DESC", Huella.class)
                    .setParameter("id", idUsuario)
                    .list();
        } catch (Exception e) {
            System.err.println("Error al recuperar historial de huellas: " + e.getMessage());
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }

    /**
     * Elimina un registro de huella específico de la base de datos.
     * @param h Instancia de la entidad a borrar.
     */
    public void borrarRegistro(Huella h) {
        huellaDAO.eliminar(h);
    }

    /**
     * Genera las métricas clave para el panel de control (Dashboard).
     * Realiza cálculos de agregación (SUM y COUNT) directamente en el motor de
     * persistencia para obtener el impacto total de CO2 y el volumen de actividades.
     * @param usuarioId ID del usuario para el que se calculan las estadísticas.
     * @return Un {@link Map} que contiene los pares "total" (impacto) y "conteo" (nº registros).
     */
    public Map<String, Double> obtenerEstadisticas(long usuarioId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Map<String, Double> stats = new HashMap<>();

            // Cálculo del sumatorio total de emisiones registradas
            Double total = session.createQuery(
                            "SELECT SUM(h.valor) FROM Huella h WHERE h.idUsuario.id = :id", Double.class)
                    .setParameter("id", (int)usuarioId)
                    .getSingleResult();

            stats.put("total", total != null ? total.doubleValue() : 0.0);

            // Conteo de la cantidad de registros realizados por el usuario
            Long actividades = session.createQuery(
                            "SELECT COUNT(h) FROM Huella h WHERE h.idUsuario.id = :id", Long.class)
                    .setParameter("id", (int)usuarioId)
                    .getSingleResult();

            stats.put("conteo", actividades != null ? actividades.doubleValue() : 0.0);

            return stats;
        } catch (Exception e) {
            // Gestión de errores: devolvemos valores neutros para evitar fallos en la vista
            System.err.println("Fallo al calcular estadísticas de huella: " + e.getMessage());
            Map<String, Double> errorStats = new HashMap<>();
            errorStats.put("total", 0.0);
            errorStats.put("conteo", 0.0);
            return errorStats;
        }
    }

    /**
     * Consulta simplificada del impacto total acumulado.
     * Delega en el DAO la ejecución de la consulta HQL específica.
     * @param idUsuario Identificador del usuario.
     * @return Valor numérico del impacto total en CO2.
     */
    public double consultarImpactoTotal(int idUsuario) {
        return huellaDAO.obtenerImpactoTotal(idUsuario);
    }

    /**
     * Recupera el listado de huellas utilizando la lógica optimizada del DAO.
     * Ideal para visualizaciones masivas de datos como tablas o listados de análisis.
     * @param idUsuario ID del usuario.
     * @return Lista de entidades {@link Huella}.
     */
    public List<Huella> obtenerHuellasPorUsuario(int idUsuario) {
        return huellaDAO.listarPorUsuario(idUsuario);
    }
}