package edu.thepower.practicarExamen2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class ServidorPedidos {
    // Almacén de pedidos: ID -> Descripción
    private static Map<Integer, String> pedidos = Collections.synchronizedMap(new TreeMap<>());
    private static int contadorIds = 1; // Para generar IDs automáticos

    public static void main(String[] args) {
        int puerto = 5000;
        try (ServerSocket server = new ServerSocket(puerto)) {
            System.out.println("Servidor de PEDIDOS escuchando en puerto " + puerto);
            while (true) {
                Socket socket = server.accept();
                // Lanzamos un hilo por cada cliente conectado
                new Thread(new GestorCliente(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("Error en servidor: " + e.getMessage());
        }
    }

    // Clase interna para gestionar cada conexión
    static class GestorCliente implements Runnable {
        private Socket socket;

        public GestorCliente(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            String clienteIP = socket.getInetAddress().toString();
            System.out.println("Cliente conectado: " + clienteIP);

            try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter pw = new PrintWriter(socket.getOutputStream(), true)) {

                pw.println("BIENVENIDO AL SISTEMA DE PEDIDOS. Comandos: NUEVO <item>, VER <id>, LISTAR, BORRAR <id>, SALIR");

                String linea;
                while ((linea = br.readLine()) != null) {
                    // Dividimos el comando y los argumentos
                    String[] partes = linea.trim().split("\\s+", 2);
                    String comando = partes[0].toUpperCase();
                    String respuesta = "";

                    switch (comando) {
                        case "NUEVO" -> {
                            if (partes.length > 1) {
                                int id;
                                synchronized (pedidos) { id = contadorIds++; }
                                pedidos.put(id, partes[1]);
                                respuesta = "OK - Pedido creado con ID: " + id;
                            } else {
                                respuesta = "ERROR - Uso: NUEVO <descripción del producto>";
                            }
                        }
                        case "VER" -> {
                            if (partes.length > 1) {
                                try {
                                    int id = Integer.parseInt(partes[1]);
                                    String producto = pedidos.get(id);
                                    respuesta = (producto != null) ? "PEDIDO " + id + ": " + producto : "ERROR - No existe el pedido " + id;
                                } catch (NumberFormatException e) {
                                    respuesta = "ERROR - El ID debe ser un número";
                                }
                            } else {
                                respuesta = "ERROR - Uso: VER <id>";
                            }
                        }
                        case "LISTAR" -> {
                            if (pedidos.isEmpty()) {
                                respuesta = "La lista de pedidos está vacía.";
                            } else {
                                StringBuilder sb = new StringBuilder("LISTA DE PEDIDOS: ");
                                synchronized (pedidos) {
                                    for (Map.Entry<Integer, String> entrada : pedidos.entrySet()) {
                                        sb.append("[").append(entrada.getKey()).append(": ").append(entrada.getValue()).append("] ");
                                    }
                                }
                                respuesta = sb.toString();
                            }
                        }
                        case "BORRAR" -> {
                            if (partes.length > 1) {
                                try {
                                    int id = Integer.parseInt(partes[1]);
                                    if (pedidos.remove(id) != null) {
                                        respuesta = "OK - Pedido " + id + " eliminado.";
                                    } else {
                                        respuesta = "ERROR - No se encontró el pedido para borrar.";
                                    }
                                } catch (NumberFormatException e) {
                                    respuesta = "ERROR - El ID debe ser un número";
                                }
                            }
                        }
                        case "SALIR" -> respuesta = "Adios";
                        default -> respuesta = "ERROR - Comando no reconocido.";
                    }

                    pw.println(respuesta);
                    if (comando.equals("SALIR")) break;
                }
            } catch (IOException e) {
                System.err.println("Error con cliente " + clienteIP + ": " + e.getMessage());
            }
        }
    }
}