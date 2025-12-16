package edu.thepower.practicarExamen2;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientePedidos {
    public static void main(String[] args) {
        String host = "localhost";
        int puerto = 5000;

        try (Socket socket = new Socket(host, puerto);
             BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
             Scanner sc = new Scanner(System.in)) {

            // Leer mensaje de bienvenida
            System.out.println("Servidor dice: " + br.readLine());

            String entradaUsuario;
            do {
                System.out.print("\nIntroduce comando > ");
                entradaUsuario = sc.nextLine();
                
                // Enviar al servidor
                pw.println(entradaUsuario);
                
                // Leer respuesta
                String respuesta = br.readLine();
                System.out.println("Servidor > " + respuesta);

            } while (!entradaUsuario.trim().equalsIgnoreCase("SALIR"));

        } catch (IOException e) {
            System.err.println("No se pudo conectar al servidor: " + e.getMessage());
        }
    }
}