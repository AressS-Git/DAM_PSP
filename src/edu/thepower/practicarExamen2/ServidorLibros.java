package edu.thepower.practicarExamen2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class ServidorLibros {
    // Usamos un Map sincronizado para evitar problemas de concurrencia entre hilos
    private static Map<String, String> biblioteca = Collections.synchronizedMap(new TreeMap<>());

    public static void main(String[] args) {
        int puerto = 5000;
        try (ServerSocket server = new ServerSocket(puerto)) {
            System.out.println("Servidor de Biblioteca iniciado en puerto " + puerto);
            
            while (true) {
                Socket socket = server.accept();
                // Lanzamos un hilo por cada cliente conectado
                new Thread(() -> gestionarCliente(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("Error en servidor: " + e.getMessage());
        }
    }

    private static void gestionarCliente(Socket socket) {
        String clienteIP = socket.getInetAddress().toString();
        System.out.println("Cliente conectado: " + clienteIP);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter pw = new PrintWriter(socket.getOutputStream(), true)) {

            String linea;
            while ((linea = br.readLine()) != null) {
                // Dividimos el comando: "add 123 El Quijote" -> ["add", "123", "El Quijote"]
                String[] partes = linea.split("\\s+", 3);
                String comando = partes[0].toLowerCase();
                String respuesta;

                respuesta = switch (comando) {
                    case "add" -> {
                        if (partes.length == 3) {
                            biblioteca.put(partes[1], partes[2]);
                            yield "OK - Libro añadido: " + partes[2];
                        } else {
                            yield "ERROR - Uso: add <isbn> <titulo>";
                        }
                    }
                    case "bus" -> {
                        if (partes.length == 2) {
                            yield biblioteca.getOrDefault(partes[1], "ERROR - Libro no encontrado");
                        } else {
                            yield "ERROR - Uso: bus <isbn>";
                        }
                    }
                    case "lis" -> {
                        if (biblioteca.isEmpty()) yield "La biblioteca está vacía.";
                        StringBuilder sb = new StringBuilder("LISTADO:\n");
                        biblioteca.forEach((isbn, titulo) -> 
                            sb.append("ISBN: ").append(isbn).append(" - ").append(titulo).append("\n")
                        );
                        yield sb.toString();
                    }
                    case "salir" -> "bye";
                    default -> "ERROR - Comando desconocido (use: add, bus, lis, salir)";
                };

                pw.println(respuesta);
                if (respuesta.equals("bye")) break;
            }
        } catch (IOException e) {
            System.err.println("Error con cliente " + clienteIP);
        }
    }
}