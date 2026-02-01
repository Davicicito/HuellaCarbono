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

/**
 * Controlador para la vista de Recomendaciones.
 * A diferencia de otras vistas, esta genera su contenido de forma dinámica mediante
 * programación (Componentes "on-the-fly"). Analiza un catálogo de consejos y los
 * renderiza como tarjetas visuales interactivas en un contenedor flexible.
 */
public class RecomendacionesViewController {

    @FXML private FlowPane flowRecomendaciones;

    /**
     * Inicializa la vista disparando la generación de los componentes visuales.
     */
    @FXML
    public void initialize() {
        generarRecomendaciones();
    }

    /**
     * Define y despliega el listado de consejos ambientales.
     * Crea una colección de objetos internos de tipo Consejo y coordina su
     * transformación en nodos visuales (VBox) que se añaden al panel principal.
     */
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

    /**
     * Construye programáticamente el árbol de nodos para una tarjeta de recomendación.
     * Aplica clases de estilo CSS dinámicas según la categoría del consejo para
     * lograr una tematización visual (colores por tipo de actividad).
     * @param c El objeto de datos con la información del consejo.
     * @return Un contenedor VBox maquetado y estilizado.
     */
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

    // --- MÉTODOS DE NAVEGACIÓN Y GESTIÓN DE SESIÓN ---

    @FXML private void irAInicio() { cambiarEscena("/view/inicio.fxml"); }
    @FXML private void irAMisHuellas() { cambiarEscena("/view/mis_huellas.fxml"); }
    @FXML private void irAHabitos() { cambiarEscena("/view/habitos.fxml"); }
    @FXML private void irAAnalisis() { cambiarEscena("/view/analisis.fxml"); }

    /**
     * Finaliza la sesión del usuario actual y limpia el contexto de seguridad.
     */
    @FXML
    private void handleLogout() {
        Sesion.getInstancia().setUsuario(null);
        cambiarEscena("/view/login.fxml");
    }

    /**
     * Ejecuta la transición entre vistas, asegurando la propagación de la hoja de estilos.
     * @param fxml Destino de la navegación.
     */
    private void cambiarEscena(String fxml) {
        try {
            Stage stage = (Stage) flowRecomendaciones.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Clase interna de apoyo para estructurar los datos de los consejos
     * antes de su renderizado.
     */
    private static class Consejo {
        String titulo, categoria, impacto, icono;
        Consejo(String t, String c, String i, String ic) {
            this.titulo = t; this.categoria = c; this.impacto = i; this.icono = ic;
        }
    }
}