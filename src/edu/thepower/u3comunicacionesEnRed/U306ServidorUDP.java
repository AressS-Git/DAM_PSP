package edu.thepower.u3comunicacionesEnRed;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class U306ServidorUDP {

    public static void main(String[] args) {
        try(DatagramSocket ds = new DatagramSocket(2100)) {
            System.out.println("Servidor escuchando en puerto 2100");
            byte[] data = new byte[1024];
            DatagramPacket dp = new DatagramPacket(data, data.length);
            ds.receive(dp);
            String mensaje = new String(dp.getData(), 0, dp.getLength());
            System.out.println("Mensaje recibido: " + mensaje);
            // Respuesta al mensaje recibido
            String ack = "ACK - " + mensaje;
            byte[] dataAck = ack.getBytes();
            InetAddress host = dp.getAddress();
            int puerto = dp.getPort();
            DatagramPacket dtp = new DatagramPacket(dataAck, dataAck.length, host, puerto);
            ds.send(dtp);
        } catch(IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }
}
