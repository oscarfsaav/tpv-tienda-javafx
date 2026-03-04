package com.tpv.controlador;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;
    @FXML private Button btnEntrar;

    @FXML
    public void initialize() {
        // ¿Qué pasa al pulsar el botón ENTRAR?
        btnEntrar.setOnAction(event -> verificarCredenciales());
    }

    private void verificarCredenciales() {
        String usuario = txtUsuario.getText();
        String password = txtPassword.getText();

        // Comprobamos la seguridad (Usuario: admin | Clave: 1234)
        if (usuario.equals("admin") && password.equals("1234")) {
            abrirTPV();
        } else {
            // Si falla, mostramos mensaje de error y vaciamos la contraseña
            lblError.setText("Usuario o contraseña incorrectos.");
            txtPassword.clear();
        }
    }

    private void abrirTPV() {
        try {
            // 1. Cargamos tu pantalla principal del TPV
            // IMPORTANTE: Asegúrate de que esta ruta apunta bien a tu VistaTPV.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/VistaTPV.fxml")); 
            Parent root = loader.load();

            // 2. Creamos la nueva ventana en grande
            Stage stageTPV = new Stage();
            stageTPV.setTitle("Mi TPV Profesional - Sesión Iniciada");
            stageTPV.setScene(new Scene(root, 950, 600));
            stageTPV.show();

            // 3. Cerramos la ventanita pequeña del Login
            Stage stageActual = (Stage) btnEntrar.getScene().getWindow();
            stageActual.close();

        } catch (IOException e) {
            System.out.println("Error al cargar el TPV: " + e.getMessage());
            lblError.setText("Error interno al abrir la aplicación.");
            e.printStackTrace();
        }
    }
}
