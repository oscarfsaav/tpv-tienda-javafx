package com.tpv.db;

import com.tpv.modelo.Producto;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class VentaDAO {

    // Ruta de tu base de datos SQLite
    private final String URL = "jdbc:sqlite:tienda.db";

    public boolean registrarVenta(double total, List<Producto> ticket) {
        // Conectamos a la base de datos
        try (Connection con = DriverManager.getConnection(URL)) {
            
            // ¡EL TRUCO PROFESIONAL! Desactivamos el autoguardado para crear una Transacción.
            // Así, si algo falla a medias, no se guardará el ticket roto.
            con.setAutoCommit(false); 

            try {
                // 1. Guardar la Venta general (solo el total)
                String sqlVenta = "INSERT INTO ventas (total) VALUES (?)";
                int idVentaGenerado = -1;
                
                // RETURN_GENERATED_KEYS nos devuelve el ID automático que le ha puesto SQLite al ticket
                try (PreparedStatement pstmtVenta = con.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                    pstmtVenta.setDouble(1, total);
                    pstmtVenta.executeUpdate();
                    
                    try (ResultSet rs = pstmtVenta.getGeneratedKeys()) {
                        if (rs.next()) {
                            idVentaGenerado = rs.getInt(1); // Guardamos el ID del ticket
                        }
                    }
                }

                // 2. Guardar las líneas de venta y 3. Restar el stock
                String sqlLinea = "INSERT INTO lineas_venta (id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
                String sqlStock = "UPDATE productos SET stock = stock - ? WHERE id = ?";

                try (PreparedStatement pstmtLinea = con.prepareStatement(sqlLinea);
                     PreparedStatement pstmtStock = con.prepareStatement(sqlStock)) {
                    
                    for (Producto p : ticket) {
                        // A) Insertamos el producto en el ticket detallado
                        pstmtLinea.setInt(1, idVentaGenerado);
                        pstmtLinea.setInt(2, p.getId());
                        pstmtLinea.setInt(3, p.getCantidad());
                        pstmtLinea.setDouble(4, p.getPrecio());
                        pstmtLinea.setDouble(5, p.getPrecio() * p.getCantidad());
                        pstmtLinea.executeUpdate();

                        // B) Le restamos al almacén la cantidad que se ha llevado el cliente
                        pstmtStock.setInt(1, p.getCantidad());
                        pstmtStock.setInt(2, p.getId());
                        pstmtStock.executeUpdate();
                    }
                }

                // Si hemos llegado hasta aquí sin errores, ¡CONFIRMAMOS LOS CAMBIOS!
                con.commit(); 
                return true;

            } catch (SQLException e) {
                // Si algo ha fallado, damos marcha atrás a todo (Rollback)
                con.rollback(); 
                System.out.println("Error en la transacción, se ha deshecho la venta: " + e.getMessage());
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Error de conexión al registrar venta: " + e.getMessage());
            return false;
        }
    }
}