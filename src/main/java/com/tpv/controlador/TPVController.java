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

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.util.List;

// ¡NUEVAS IMPORTACIONES PARA ARREGLAR EXCEL!
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

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

        btnCobrar.setOnAction(event -> {
            if (listaTicket.isEmpty()) {
                javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                alerta.setTitle("Aviso");
                alerta.setHeaderText(null);
                alerta.setContentText("No hay ningún producto en el ticket para cobrar.");
                alerta.showAndWait();
            } else {
                
                // 1. Registramos la venta en la Base de Datos y restamos Stock
                com.tpv.db.VentaDAO ventaDAO = new com.tpv.db.VentaDAO();
                boolean guardadoBD = ventaDAO.registrarVenta(precioTotal, listaTicket);
                
                if (!guardadoBD) {
                    javafx.scene.control.Alert alertaError = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                    alertaError.setHeaderText("Error en Base de Datos");
                    alertaError.setContentText("No se ha podido guardar la venta. Revisa la consola.");
                    alertaError.showAndWait();
                    return;
                }

                // 2. ABRIR VENTANA PARA GUARDAR EL CSV
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Guardar Ticket en CSV");
                fileChooser.setInitialFileName("ticket_venta.csv"); 
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV (*.csv)", "*.csv"));

                Stage stage = (Stage) btnCobrar.getScene().getWindow();
                File file = fileChooser.showSaveDialog(stage);

                if (file != null) {
                    // ¡AQUÍ ESTÁ LA MAGIA PARA EXCEL! 
                    // Usamos UTF-8 explícitamente y añadimos el BOM para los acentos.
                    try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
                        
                        // Escribimos el caracter BOM invisible al principio
                        writer.write('\ufeff');
                        
                        // Usamos punto y coma (;) en lugar de coma (,)
                        writer.println("Producto;Precio Unitario;Cantidad;Subtotal");
                        
                        for (Producto p : listaTicket) {
                            double subtotal = p.getPrecio() * p.getCantidad();
                            // Reemplazamos los puntos de los precios por comas para que Excel los sume bien si hace falta
                            String precioStr = String.format("%.2f", p.getPrecio());
                            String subtotalStr = String.format("%.2f", subtotal);
                            
                            writer.println(p.getNombre() + ";" + precioStr + " €;" + p.getCantidad() + ";" + subtotalStr + " €");
                        }
                        
                        writer.println(";;;"); 
                        writer.println("TOTAL;;;" + String.format("%.2f", precioTotal) + " €");
                        
                    } catch (IOException e) {
                        System.out.println("Error al guardar el CSV: " + e.getMessage());
                    }
                }

                // 3. MOSTRAR MENSAJE DE ÉXITO Y LIMPIAR
                javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                alerta.setTitle("Pago completado");
                alerta.setHeaderText("¡Cobro realizado con éxito!");
                alerta.setContentText(String.format("Total cobrado: %.2f €\nSe ha actualizado el stock en la base de datos.", precioTotal));
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

 // --- EL DIBUJANTE DE BOTONES (VERSIÓN PREMIUM UI/UX) ---
    private void cargarBotonesProductos(String categoriaFiltro) {
        panelProductos.getChildren().clear(); 

        com.tpv.db.ProductoDAO dao = new com.tpv.db.ProductoDAO();
        List<Producto> listaProductos = dao.obtenerTodos();
        
        String textoBuscado = txtBuscar.getText().toLowerCase();

        for (Producto p : listaProductos) {
            boolean coincideCategoria = categoriaFiltro.equals("Todas") || p.getCategoria().equalsIgnoreCase(categoriaFiltro);
            boolean coincideTexto = textoBuscado.isEmpty() || p.getNombre().toLowerCase().contains(textoBuscado);

            if (coincideCategoria && coincideTexto) {
                
                Button btnProducto = new Button();
                btnProducto.setPrefSize(130, 130); 
                btnProducto.getStyleClass().add("boton-producto"); // Conectamos con el CSS
                
                VBox cajaContenido = new VBox();
                cajaContenido.setAlignment(Pos.CENTER);
                cajaContenido.setSpacing(5); 
                
                ImageView imagenVista = new ImageView();
                imagenVista.setFitHeight(60);
                imagenVista.setFitWidth(60);
                imagenVista.setPreserveRatio(true);
                
                // Buscamos la foto en la carpeta /imagenes/
                try {
                    String nombreFoto = p.getNombre().toLowerCase().replace(" ", "_") + ".png";
                    java.net.URL urlImagen = getClass().getResource("/imagenes/" + nombreFoto);
                    if (urlImagen != null) {
                        imagenVista.setImage(new Image(urlImagen.toExternalForm()));
                    }
                } catch (Exception e) {
                    System.out.println("No se encontró imagen para: " + p.getNombre());
                }

                Label lblNombre = new Label(p.getNombre());
                lblNombre.getStyleClass().add("etiqueta-nombre");
                
                Label lblPrecio = new Label(String.format("%.2f €", p.getPrecio()));
                lblPrecio.getStyleClass().add("etiqueta-precio");

                cajaContenido.getChildren().addAll(imagenVista, lblNombre, lblPrecio);
                btnProducto.setGraphic(cajaContenido);
                
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