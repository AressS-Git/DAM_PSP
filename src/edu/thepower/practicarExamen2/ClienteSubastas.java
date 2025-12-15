package edu.thepower.practicarExamen2;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClienteSubastas {
    private static final String HOST = "localhost";
    private static final int PORT = 5555;

    public static void main(String[] args) {
        try(
                Socket socket = new Socket(HOST, PORT);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true)
                ) {
            System.out.println("Conexión establecida con el servidor");
            Scanner sc = new Scanner(System.in);
            String comando;
            do {
                System.out.println("Comandos disponibles (ver/pujar/listar/salir):");
                System.out.println("ver <nombre_articulo>");
                System.out.println("pujar <nombre_articulo> <cantidad>");
                System.out.println("listar");
                System.out.println("salir");
                System.out.print("Introduce un comando: ");
                comando = sc.nextLine();
                printWriter.println(comando);
                String lineaRespuesta = bufferedReader.readLine();

                if (lineaRespuesta != null) {
                    // Imprimir la primera línea de la respuesta
                    System.out.println(lineaRespuesta);
                    try {
                        // Imprimir líneas restantes de la respuesta (porque el comando listar devuelve varias líneas)
                        while (bufferedReader.ready()) {
                            System.out.println(bufferedReader.readLine());
                        }
                    } catch (IOException e) {
                        // Manejo de excepción si falla la lectura posterior
                        System.err.println("Error al leer la respuesta completa: " + e.getMessage());
                    }
                };
            } while(!comando.trim().equalsIgnoreCase("salir") );
            System.out.println("Cerrando la conexión con el servidor...");
        } catch (IOException e) {
            System.err.println("Error al conectarse al servidor: " + e.getMessage());
        }
    }
}
