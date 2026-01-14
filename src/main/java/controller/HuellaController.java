package controller;

import model.Huella;
import services.HuellaService;
import java.util.List;

public class HuellaController {
    private final HuellaService huellaService = new HuellaService();

    public void guardarHuella(Huella h) {
        huellaService.registrarNuevaHuella(h); //
    }

    public List<Huella> getHistorial(int idUsuario) {
        return huellaService.obtenerHistorial(idUsuario); // [cite: 23, 86]
    }

    public double getImpactoTotal(int idUsuario) {
        return huellaService.consultarImpactoTotal(idUsuario); // [cite: 17, 19]
    }
}
