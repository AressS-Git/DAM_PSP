package edu.thepower.practicarExamen2;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class RestaurantClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 3000;

        try (Socket socket = new Socket(host, port);
             BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
             Scanner sc = new Scanner(System.in)) {

            // Leer mensaje de bienvenida
            System.out.println("SERVIDOR: " + br.readLine());

            String inputUsuario;
            do {
                System.out.print("> ");
                inputUsuario = sc.nextLine();
                pw.println(inputUsuario);

                // Leer respuesta del servidor
                String respuesta = br.readLine();
                System.out.println("CAMARERO: " + respuesta);

            } while (!inputUsuario.equalsIgnoreCase("SALIR"));

        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }
}