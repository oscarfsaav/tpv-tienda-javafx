package com.tpv.controlador;

import com.tpv.db.ProductoDAO;
import com.tpv.modelo.Producto;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;

// ¡NUEVAS IMPORTACIONES PARA GUARDAR ARCHIVOS!
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.List;

public class TPVController {

    @FXML private TextField txtBuscar;
    @FXML private FlowPane panelProductos;
    @FXML private Button btnVaciar;
    @FXML private TableView<Producto> tablaTicket;
    @FXML private TableColumn<Producto, String> colProducto;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Integer> colCant;
    @FXML private Label lblTotal;
    @FXML private Button btnCobrar;

    @FXML private Button btnCatTodas;
    @FXML private Button btnCatBebidas;
    @FXML private Button btnCatComida;
    @FXML private Button btnCatPostres;

    private ObservableList<Producto> listaTicket = FXCollections.observableArrayList();
    private double precioTotal = 0.0;
    private String categoriaActual = "Todas";

    @FXML
    public void initialize() {
        System.out.println("¡Iniciando TPV...");

        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colCant.setCellValueFactory(new PropertyValueFactory<>("cantidad")); 
        
        tablaTicket.setItems(listaTicket);

        cargarBotonesProductos(categoriaActual);

        btnCatTodas.setOnAction(e -> cambiarCategoria("Todas"));
        btnCatBebidas.setOnAction(e -> cambiarCategoria("Bebidas"));
        btnCatComida.setOnAction(e -> cambiarCategoria("Comida"));
        btnCatPostres.setOnAction(e -> cambiarCategoria("Postres"));

        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            cargarBotonesProductos(categoriaActual);
        });

        btnVaciar.setOnAction(event -> {
            listaTicket.clear();
            precioTotal = 0.0;
            lblTotal.setText("TOTAL: 0.00 €");
        });

        // --- BOTÓN COBRAR ACTUALIZADO ---
        btnCobrar.setOnAction(event -> {
            if (listaTicket.isEmpty()) {
                javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                alerta.setTitle("Aviso");
                alerta.setHeaderText(null);
                alerta.setContentText("No hay ningún producto en el ticket para cobrar.");
                alerta.showAndWait();
            } else {
                // 1. ABRIR VENTANA PARA GUARDAR EL CSV
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Guardar Ticket en CSV");
                // Nombre por defecto
                fileChooser.setInitialFileName("ticket_venta.csv"); 
                // Filtro para que solo deje guardar en formato CSV
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV (*.csv)", "*.csv"));

                // Obtenemos la ventana actual para mostrar encima el diálogo
                Stage stage = (Stage) btnCobrar.getScene().getWindow();
                File file = fileChooser.showSaveDialog(stage);

                // Si el usuario ha elegido una ruta y no ha cancelado...
                if (file != null) {
                    try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                        // Escribimos la cabecera del CSV (las columnas)
                        writer.println("Producto,Precio Unitario,Cantidad,Subtotal");
                        
                        // Recorremos el ticket y escribimos cada línea
                        for (Producto p : listaTicket) {
                            double subtotal = p.getPrecio() * p.getCantidad();
                            writer.println(p.getNombre() + "," + p.getPrecio() + " €," + p.getCantidad() + "," + subtotal + " €");
                        }
                        
                        // Añadimos el total al final
                        writer.println(",,,"); // Celdas vacías para alinear
                        writer.println("TOTAL,,," + precioTotal + " €");
                        
                    } catch (IOException e) {
                        System.out.println("Error al guardar el archivo CSV: " + e.getMessage());
                    }
                }

                // 2. MOSTRAR MENSAJE DE ÉXITO Y LIMPIAR
                javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                alerta.setTitle("Pago completado");
                alerta.setHeaderText("¡Cobro realizado con éxito!");
                alerta.setContentText(String.format("Total cobrado: %.2f €\n¡Gracias por su compra!", precioTotal));
                alerta.showAndWait();
                
                listaTicket.clear();
                precioTotal = 0.0;
                lblTotal.setText("TOTAL: 0.00 €");
                txtBuscar.clear();
            }
        });
    }

    private void cambiarCategoria(String nuevaCategoria) {
        categoriaActual = nuevaCategoria;
        cargarBotonesProductos(categoriaActual);
    }

    private void cargarBotonesProductos(String categoriaFiltro) {
        panelProductos.getChildren().clear(); 

        ProductoDAO dao = new ProductoDAO();
        List<Producto> listaProductos = dao.obtenerTodos();
        
        String textoBuscado = txtBuscar.getText().toLowerCase();

        for (Producto p : listaProductos) {
            boolean coincideCategoria = categoriaFiltro.equals("Todas") || p.getCategoria().equalsIgnoreCase(categoriaFiltro);
            boolean coincideTexto = textoBuscado.isEmpty() || p.getNombre().toLowerCase().contains(textoBuscado);

            if (coincideCategoria && coincideTexto) {
                
                Button btnProducto = new Button(p.getNombre() + "\n" + p.getPrecio() + "€");
                btnProducto.setPrefSize(120, 100); 
                btnProducto.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
                
                btnProducto.setOnAction(event -> {
                    boolean encontrado = false;
                    for (Producto prodTicket : listaTicket) {
                        if (prodTicket.getNombre().equals(p.getNombre())) {
                            prodTicket.setCantidad(prodTicket.getCantidad() + 1);
                            encontrado = true;
                            break;
                        }
                    }
                    if (!encontrado) {
                        p.setCantidad(1);
                        listaTicket.add(p);
                    }
                    precioTotal += p.getPrecio();
                    lblTotal.setText(String.format("TOTAL: %.2f €", precioTotal));
                    tablaTicket.refresh();
                });
                
                panelProductos.getChildren().add(btnProducto);
            }
        }
    }
}