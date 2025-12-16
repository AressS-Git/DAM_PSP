package edu.thepower.practicarExamen2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ClienteImagenesUDP {
    public static void main(String[] args) {
        try(DatagramSocket datagramSocket = new DatagramSocket()) {
            // Bucle para realizar el envío de 5 imágenes
            for(int i = 0; i < 5; i++) {
                // Preparar el paquete a enviar
                String nombreArchivo = "imagen" + (i + 1) + ".jpg";
                byte[] data = nombreArchivo.getBytes();
                DatagramPacket datagramPacket = new DatagramPacket(data, data.length, InetAddress.getByName("localhost"), 4500);

                //Enviar paquete
                datagramSocket.send(datagramPacket);

                // Preparar el paquete de respuesta
                byte[] dataRespuesta = new byte[1024];
                DatagramPacket datagramPacketRespuesta = new DatagramPacket(dataRespuesta, dataRespuesta.length);

                // Recibir paquete de respuesta
                datagramSocket.receive(datagramPacketRespuesta);
                String respuesta = new String(datagramPacketRespuesta.getData(), 0, datagramPacketRespuesta.getLength());

                // Imprimir la respuesta
                System.out.println("Respuesta del servidor: " + respuesta);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
