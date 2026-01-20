package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import services.HuellaService;
import utils.Sesion;
import java.util.*;

public class RecomendacionesViewController {

    @FXML private FlowPane flowRecomendaciones;

    @FXML
    public void initialize() {
        generarRecomendaciones();
    }

    private void generarRecomendaciones() {
        flowRecomendaciones.getChildren().clear();

        List<Consejo> todosLosConsejos = Arrays.asList(
                new Consejo("Usa bicicleta o camina", "Transporte", "-30 kg CO₂", "🚲"),
                new Consejo("Opta por transporte público", "Transporte", "-45 kg CO₂", "🚌"),
                new Consejo("Apaga dispositivos eléctricos", "Energía", "-10 kg CO₂", "💡"),
                new Consejo("Usa bombillas LED", "Energía", "-15 kg CO₂", "🔌"),
                new Consejo("Reduce consumo de carne", "Alimentación", "-50 kg CO₂", "🥗"),
                new Consejo("Recicla residuos", "Residuos", "-25 kg CO₂", "♻️")
        );

        for (Consejo c : todosLosConsejos) {
            flowRecomendaciones.getChildren().add(crearTarjetaConsejo(c));
        }
    }

    private VBox crearTarjetaConsejo(Consejo c) {
        VBox card = new VBox(10);
        card.getStyleClass().add("recommendation-card");

        HBox topRow = new HBox();
        topRow.getStyleClass().add("card-top-row");

        Label titulo = new Label(c.titulo);
        titulo.getStyleClass().add("card-title-text");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label icono = new Label(c.icono);
        icono.getStyleClass().add("card-icon");

        topRow.getChildren().addAll(titulo, spacer, icono);

        Label categoria = new Label(c.categoria.toUpperCase());
        categoria.getStyleClass().add("tag-category");
        categoria.getStyleClass().add("tag-" + c.categoria.toLowerCase().replace("í", "i"));

        Label impacto = new Label("Impacto estimado: " + c.impacto);
        impacto.getStyleClass().add("card-impact-text");

        card.getChildren().addAll(topRow, categoria, impacto);
        return card;
    }

    // --- MÉTODOS DE NAVEGACIÓN ---

    @FXML private void irAInicio() { cambiarEscena("/view/inicio.fxml"); }
    @FXML private void irAMisHuellas() { cambiarEscena("/view/mis_huellas.fxml"); }
    @FXML private void irAHabitos() { cambiarEscena("/view/habitos.fxml"); }
    @FXML private void irAAnalisis() { cambiarEscena("/view/analisis.fxml"); }

    @FXML
    private void handleLogout() {
        Sesion.getInstancia().setUsuario(null);
        cambiarEscena("/view/login.fxml");
    }

    private void cambiarEscena(String fxml) {
        try {
            Stage stage = (Stage) flowRecomendaciones.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Scene scene = new Scene(loader.load());
            // Aplicamos el CSS global
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class Consejo {
        String titulo, categoria, impacto, icono;
        Consejo(String t, String c, String i, String ic) {
            this.titulo = t; this.categoria = c; this.impacto = i; this.icono = ic;
        }
    }
}