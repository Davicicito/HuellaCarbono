package services;

import DAO.HuellaDAO;
import model.Huella;
import java.util.List;

public class HuellaService {
    private final HuellaDAO huellaDAO;

    public HuellaService() {
        this.huellaDAO = new HuellaDAO();
    }

    public void registrarNuevaHuella(Huella h) {
        huellaDAO.guardar(h); // [cite: 9, 83]
    }

    public List<Huella> obtenerHistorial(int idUsuario) {
        return huellaDAO.listarPorUsuario(idUsuario); // [cite: 23, 86]
    }

    public double consultarImpactoTotal(int idUsuario) {
        // Lógica de negocio: obtener el cálculo acumulado [cite: 19, 20]
        return huellaDAO.obtenerImpactoTotal(idUsuario);
    }

    public void borrarRegistro(Huella h) {
        huellaDAO.eliminar(h); //
    }
}