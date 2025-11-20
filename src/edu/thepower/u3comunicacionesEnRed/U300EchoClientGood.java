package edu.thepower.u3comunicacionesEnRed;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class U300EchoClientGood {
    public static void main(String[] args) {
        try(Socket socket = new Socket("localhost", 3000);) {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            Scanner sc = new Scanner(System.in);
            String linea;
            do {
                linea = sc.nextLine().trim();
                pw.println(linea);
                System.out.println(br.readLine());
            } while(!linea.equalsIgnoreCase("/salir"));
        } catch (IOException e) {
            System.err.println("Error con la conexión con el server: " + e.getMessage());
        }
        System.out.println("Comunicación con el servidor finalizada");
    }
}
