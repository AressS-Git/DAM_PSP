package edu.thepower.practicarExamen2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RestaurantServer {
    // Mapa estático para el menú (Plato -> Precio)
    private static final Map<String, Double> MENU = new HashMap<>();

    static {
        MENU.put("hamburguesa", 12.50);
        MENU.put("pizza", 10.00);
        MENU.put("ensalada", 8.50);
        MENU.put("refresco", 2.50);
        MENU.put("agua", 1.50);
    }

    public static void main(String[] args) {
        // Usamos un Pool de hilos para eficiencia, similar a U4P01PoolServer
        ExecutorService pool = Executors.newFixedThreadPool(10);
        int puerto = 3000;

        try (ServerSocket server = new ServerSocket(puerto)) {
            System.out.println("COCINA ABIERTA: Servidor escuchando en puerto " + puerto);

            while (true) {
                Socket socket = server.accept();
                // Enviamos la gestión del cliente al pool
                pool.submit(() -> gestionarMesa(socket));
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }

    private static void gestionarMesa(Socket socket) {
        String mesa = socket.getInetAddress() + ":" + socket.getPort();
        System.out.println("Nueva mesa conectada: " + mesa);
        double cuentaTotal = 0.0;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter pw = new PrintWriter(socket.getOutputStream(), true)) {

            pw.println("BIENVENIDO A 'EL CODIGO GOURMET'. Comandos: VER, PEDIR <plato>, CUENTA, SALIR");

            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split("\\s+", 2);
                String comando = partes[0].trim().toUpperCase();
                String respuesta;

                // Estructura switch similar a U3P07ServerDiccionario
                switch (comando) {
                    case "VER" -> {
                        StringBuilder sb = new StringBuilder("MENU DEL DIA: ");
                        MENU.forEach((k, v) -> sb.append(k).append(" (").append(v).append("€), "));
                        respuesta = sb.substring(0, sb.length() - 2);
                    }
                    case "PEDIR" -> {
                        if (partes.length > 1) {
                            String plato = partes[1].toLowerCase();
                            if (MENU.containsKey(plato)) {
                                double precio = MENU.get(plato);
                                cuentaTotal += precio;
                                respuesta = "Oído cocina: " + plato + ". Subtotal: " + cuentaTotal + "€";
                            } else {
                                respuesta = "Lo sentimos, no tenemos " + plato + " en el menú.";
                            }
                        } else {
                            respuesta = "Error: Debe especificar el plato (Ej: PEDIR pizza)";
                        }
                    }
                    case "CUENTA" -> respuesta = "El total de su mesa es: " + cuentaTotal + "€";
                    case "SALIR" -> {
                        pw.println("Gracias por su visita. Total final: " + cuentaTotal + "€");
                        return; // Rompe el ciclo y cierra el socket al salir del try-with-resources
                    }
                    default -> respuesta = "Comando no reconocido. Intente: VER, PEDIR <plato>, CUENTA, SALIR";
                }
                pw.println(respuesta);
            }
        } catch (IOException e) {
            System.err.println("Mesa desconectada abruptamente: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException e) { /* Ignorar */ }
            System.out.println("Mesa liberada: " + mesa);
        }
    }
}