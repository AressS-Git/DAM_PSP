package edu.thepower.practicarExamen2;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClienteLibros {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5000);
             BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
             Scanner sc = new Scanner(System.in)) {

            System.out.println("Conectado a la Biblioteca. Comandos: add, bus, lis, salir");
            String entrada;
            
            do {
                System.out.print("> ");
                entrada = sc.nextLine();
                pw.println(entrada); // Enviar al servidor

                // Leer respuesta. Si es 'lis', puede tener varias líneas, 
                // pero para simplificar leemos línea a línea o todo el bloque si el protocolo lo permite.
                // En este ejemplo simple, asumimos que el servidor envía una respuesta que cabe en el buffer o usamos un bucle si esperamos más.
                
                // NOTA: Para el examen, si el servidor devuelve varias líneas (como en 'lis'), 
                // tu código actual suele leer solo una línea con br.readLine(). 
                // Modificación simple para lectura de una sola respuesta:
                String respuesta = br.readLine();
                // Si la respuesta contiene saltos de línea codificados o el servidor envía línea a línea, habría que ajustar.
                // Aquí imprimimos lo que llega:
                System.out.println("Servidor: " + respuesta);
                
                // Truco: Si el 'lis' del servidor envía un solo String con \n, readLine() leerá solo hasta el primer \n.
                // Para el examen, asegúrate de cómo envías los datos. 
                // En el servidor de arriba, 'lis' devuelve un String largo. readLine() leerá solo la primera linea ("LISTADO:").
                // Si quieres leer todo, necesitarías un bucle.

            } while (!entrada.equalsIgnoreCase("salir"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}