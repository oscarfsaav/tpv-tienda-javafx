package com.tpv.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    // Nombre del archivo de la base de datos
    private static final String URL = "jdbc:sqlite:tienda.db";

    public static Connection conectar() {
        Connection conexion = null;
        try {
            // Intentamos establecer la conexión
            conexion = DriverManager.getConnection(URL);
            System.out.println("Conexión establecida con éxito a SQLite.");
        } catch (SQLException e) {
            System.out.println("Error al conectar: " + e.getMessage());
        }
        return conexion;
    }
}