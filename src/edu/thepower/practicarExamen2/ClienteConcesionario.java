package edu.thepower.practicarExamen2;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClienteConcesionario {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5000);
             BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
             Scanner sc = new Scanner(System.in)) {

            System.out.println("Conectado al servidor. " + br.readLine()); // Leer mensaje de bienvenida
            String comando;

            do {
                System.out.print("\n> ");
                comando = sc.nextLine();
                pw.println(comando); // Enviar al servidor
                
                // Leemos la respuesta. Si es multilinea (como el STOCK), deberíamos leer en bucle, 
                // pero para simplificar el examen, asumimos respuestas en una línea o usamos un delimitador.
                // Aquí leemos una línea principal:
                String respuesta = br.readLine();
                if(respuesta != null) System.out.println("Servidor: " + respuesta.replace("\\n", "\n"));

            } while (!comando.equalsIgnoreCase("SALIR"));

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}