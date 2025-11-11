package edu.thepower.u3comunicacionesEnRed;

import java.io.*;
import java.net.Socket;

public class U301EjemploSocket {
    public static void main(String[] args) {
        try(Socket socket = new Socket("whois.internic.net", 43)) {
            //Definimos aquí el Stream de salida hacia el whois
            OutputStream os = socket.getOutputStream();
            //Objeto que facilita escribir caracteres
            PrintWriter pw = new PrintWriter(os, true);
            //Stream de entrada
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            //Enviamos el dominio para el que queremos que whois identifique al propietario
            pw.println("miservidorprivado.com");
            String line = null;
            //Leer información mientras el servidor whois devuelva algo
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error al acceder al servidor");
        }
    }
}
