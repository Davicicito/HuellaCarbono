package controller;

import model.Huella;
import services.HuellaService;
import java.util.List;

public class HuellaController {
    private final HuellaService huellaService;

    public HuellaController() {
        this.huellaService = new HuellaService();
    }

    public void guardarHuella(Huella h) {
        huellaService.registrarNuevaHuella(h);
    }

    public double getImpactoTotal(int idUsuario) {
        return huellaService.consultarImpactoTotal(idUsuario);
    }

    public List<Huella> getHistorial(int idUsuario) {
        return huellaService.obtenerHistorial(idUsuario);
    }
}
