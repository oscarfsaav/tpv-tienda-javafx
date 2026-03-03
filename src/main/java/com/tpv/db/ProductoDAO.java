package com.tpv.db;

import com.tpv.modelo.Producto;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public List<Producto> obtenerTodos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos";

        try (Connection con = ConexionDB.conectar();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto p = new Producto(
                    rs.getInt("id"),
                    rs.getString("codigo_barras"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getInt("stock"),
                    rs.getString("categoria")
                );
                lista.add(p);
            }
        } catch (Exception e) {
            System.out.println("Error al obtener productos: " + e.getMessage());
        }
        return lista;
    }
}