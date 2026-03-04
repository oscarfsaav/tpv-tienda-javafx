package com.tpv.tpv_tienda;

import com.tpv.db.ConexionDB;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // AHORA ARRANCAMOS POR EL LOGIN
            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
            
            // Hacemos la ventana más pequeñita (350x400) para que parezca un login real
            Scene scene = new Scene(root, 350, 400);
            
            primaryStage.setTitle("Acceso TPV");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false); // Evitamos que puedan maximizar el login
            primaryStage.show();
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Comprobamos que la base de datos sigue viva
        ConexionDB.conectar();
        
        // Arranca la interfaz gráfica (esto llama al método start de arriba)
        launch(args);
    }
}