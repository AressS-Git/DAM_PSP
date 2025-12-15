/*
package edu.thepower.u4serviciosEnRed;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;

public class U4P04FtpClient {
    public static void main(String[] args) {
        FTPClient ftp = new FTPClient();
        // 1. Conectarse al servidor FTP
        try {
            ftp.connect("eu-central-1.sftpcloud.io"); // Cómo el puerto por defecto es 21 y nos vamos a conectar al 21, no hace falta ponerlo como argumento
            System.out.println("Conectado al servidor FTP");
            // 2. Loggearse
            if(ftp.login("990fa04831eb473b9cf4ebe4eace3ab1", "oL91IvJKCW0rjMYGgWwXtaeNqEvne27F")) {
                System.out.println("Acceso correcto al servidor FTP");
                // 3. Activar modo pasivo (cliente siempre se conecta al servidor, no al revés), los datos enviados en binario
                ftp.enterLocalPassiveMode();
                ftp.setFileType(FTPClient.BINARY_FILE_TYPE);

                // 4. Enviar un archivo
                InputStream is = new FileInputStream("C:\\Users\\AlumnoAfternoon\\Desktop\\PSP\\resources\\vocales.txt");
                String ficheroRemoto = "archivo_remoto.txt";

                if(ftp.storeFile(ficheroRemoto, is)) {
                    System.out.println("Fichero subido correctamente al servidor FTP");
                } else {
                    System.err.println("Error en la subida del archivo al servidor FTP");
                }

                // 5. Listado del contenido del servidor FTP
                FTPFile[] lista = ftp.listFiles();
                for(FTPFile f : lista) {
                    System.out.println(f.getName());
                }

                // 6. Descargar el archivo del servidor FTP
                OutputStream os = new FileOutputStream("C:\\Users\\AlumnoAfternoon\\Desktop\\PSP\\resources\\archivo_descargado_desde_ftp");
                if(ftp.retrieveFile(ficheroRemoto, os)) {
                    System.out.println("Fichero descargado correctamente del servidor FTP");
                } else {
                    System.err.println("Error en la descarga del archivo del servidor FTP");
                }
            } else {
                System.err.println("Error de acceso al servidor FTP");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
*/