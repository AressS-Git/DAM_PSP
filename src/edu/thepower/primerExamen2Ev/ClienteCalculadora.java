package edu.thepower.primerExamen2Ev;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClienteCalculadora {
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
                System.out.println("Comandos disponibles (sum/res/mul/div/fin):");
                System.out.println("Formato de las operaciones: OPERACION <operando1> <operando2>");
                System.out.println("Salir de la aplicación: fin");
                System.out.print("Introduce un comando: ");
                comando = sc.nextLine();
                printWriter.println(comando);
                String lineaRespuesta = bufferedReader.readLine();
                System.out.println(lineaRespuesta);
                System.out.println();
            } while(!comando.trim().equalsIgnoreCase("fin") );
            System.out.println("Cerrando la conexión con el servidor...");
        } catch (IOException e) {
            System.err.println("Error al conectarse al servidor: " + e.getMessage());
        }
    }
}

