package edu.thepower.practicarExamen2;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ServidorConcesionario {
    // Inventario compartido: Modelo -> Cantidad
    private static Map<String, Integer> stockCoches = Collections.synchronizedMap(new TreeMap<>());
    private static AtomicInteger totalVentas = new AtomicInteger(0); // Contador atómico para estadísticas

    // Bloque estático para inicializar datos, similar a tu ejemplo del diccionario
    static {
        stockCoches.put("toyota corolla", 5);
        stockCoches.put("ford mustang", 2);
        stockCoches.put("seat ibiza", 10);
        stockCoches.put("tesla model 3", 3);
    }

    public static void main(String[] args) {
        int puerto = 5000;
        try (ServerSocket server = new ServerSocket(puerto)) {
            System.out.println("Servidor Concesionario iniciado en puerto " + puerto);
            while (true) {
                Socket socket = server.accept();
                // Lanzamos un hilo por cliente para gestión concurrente
                new Thread(new GestorCliente(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("Error en servidor: " + e.getMessage());
        }
    }

    // Clase interna para manejar cada cliente (Runnable)
    static class GestorCliente implements Runnable {
        private Socket socket;

        public GestorCliente(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter pw = new PrintWriter(socket.getOutputStream(), true)) {
                
                String comando;
                pw.println("Bienvenido al Concesionario. Comandos: STOCK, VENDER <modelo>, INFO, SALIR");

                while ((comando = br.readLine()) != null) {
                    String[] partes = comando.split("\\s+", 2); // Dividir comando y argumento
                    String accion = partes[0].trim().toUpperCase();
                    String respuesta;

                    // Lógica principal usando switch (estilo Java moderno usado en tus ejemplos)
                    respuesta = switch (accion) {
                        case "STOCK" -> {
                            StringBuilder sb = new StringBuilder("--- Stock Disponible ---\n");
                            stockCoches.forEach((k, v) -> sb.append(k).append(": ").append(v).append(" unidades\n"));
                            yield sb.toString();
                        }
                        case "VENDER" -> {
                            if (partes.length < 2) yield "ERROR: Indica el modelo. Ejemplo: VENDER seat ibiza";
                            String modelo = partes[1].toLowerCase();
                            synchronized (stockCoches) { // Bloqueamos para asegurar integridad
                                int cantidad = stockCoches.getOrDefault(modelo, 0);
                                if (cantidad > 0) {
                                    stockCoches.put(modelo, cantidad - 1);
                                    totalVentas.incrementAndGet();
                                    yield "VENTA EXITOSA: Quedan " + (cantidad - 1) + " unidades de " + modelo;
                                } else {
                                    yield "SIN STOCK: No quedan unidades de " + modelo;
                                }
                            }
                        }
                        case "INFO" -> "Total ventas realizadas hoy: " + totalVentas.get();
                        case "SALIR" -> "Adiós";
                        default -> "Comando no reconocido.";
                    };

                    pw.println(respuesta);
                    if (accion.equals("SALIR")) break;
                }
            } catch (IOException e) {
                System.err.println("Error con cliente: " + e.getMessage());
            }
        }
    }
}