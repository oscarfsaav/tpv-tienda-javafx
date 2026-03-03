package com.tpv.tpv_tienda;

import com.tpv.db.ConexionDB;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 1. Cargar el diseño FXML que creamos en Scene Builder
        // El "/" indica que busque en la carpeta src/main/resources
        Parent root = FXMLLoader.load(getClass().getResource("/VistaTPV.fxml"));

        // 2. Crear la "Escena" (el contenido de la ventana)
        Scene scene = new Scene(root);

        // 3. Configurar la "Ventana" (Stage)
        primaryStage.setTitle("Mi TPV Profesional");
        primaryStage.setScene(scene);
        
        // Evita que la ventana se haga súper pequeña
        primaryStage.setMinWidth(950);
        primaryStage.setMinHeight(600);
        
        // 4. ¡Mostrar la ventana!
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Comprobamos que la base de datos sigue viva
        ConexionDB.conectar();
        
        // Arranca la interfaz gráfica (esto llama al método start de arriba)
        launch(args);
    }
}