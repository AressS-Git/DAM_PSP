package edu.thepower.u3comunicacionesEnRed;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class U308ServidorLatencia {
    public static void main(String[] args) {
        try(DatagramSocket ds = new DatagramSocket(2300)) {
            System.out.println("Escuchando en el puerto 2300");
            byte[] data = new byte[1024];
            DatagramPacket dp;
            while(true) {
                dp = new DatagramPacket(data, data.length);
                ds.receive(dp);
                String message = new String(dp.getData(), 0, dp.getLength());
                System.out.println("Recibido mensaje: " + message);
                String respuesta = "pong";
                byte[] dataRespuesta = respuesta.getBytes();
                InetAddress ip = dp.getAddress();
                int puerto = dp.getPort();
                DatagramPacket dpRespuesta = new DatagramPacket(dataRespuesta, dataRespuesta.length, ip, puerto);
                ds.send(dpRespuesta);
            }
        } catch(IOException e) {
            System.err.println("Error:" + e.getMessage());
        }
    }
}