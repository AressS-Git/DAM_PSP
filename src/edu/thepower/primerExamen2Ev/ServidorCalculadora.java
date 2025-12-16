package edu.thepower.primerExamen2Ev;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorCalculadora {

    // Constante de mensaje de error cuándo los operadores no son números
    public static final String ERROR_CONVERSION = "Error: los valores introducidos deben ser numéricos";

    // --- Métodos para realizar las operaciones, cada método intentará convertir los parámetros a números, si no lo consiguen saltará una excepción y mostrará un error (la constante)
    // Suma
    public static String sumar(String operando1, String operando2) {
        try {
            double operando1double = Double.parseDouble(operando1);
            double operando2double = Double.parseDouble(operando2);
            return "El resultado de la suma es: " + (operando1double + operando2double);
        } catch(NumberFormatException e) {
            return ERROR_CONVERSION;
        }
    }

    // Resta
    public static String restar(String operando1, String operando2) {
        try {
            double operando1double = Double.parseDouble(operando1);
            double operando2double = Double.parseDouble(operando2);
            return "El resultado de la resta es: " + (operando1double - operando2double);
        } catch(NumberFormatException e) {
            return ERROR_CONVERSION;
        }
    }

    // Multiplicación
    public static String multiplicar(String operando1, String operando2) {
        try {
            double operando1double = Double.parseDouble(operando1);
            double operando2double = Double.parseDouble(operando2);
            return "El resultado de la multiplicación es: " + (operando1double * operando2double);
        } catch(NumberFormatException e) {
            return ERROR_CONVERSION;
        }
    }

    // División
    public static String dividir(String operando1, String operando2) {
        try {
            double operando1double = Double.parseDouble(operando1);
            double operando2double = Double.parseDouble(operando2);
            if(operando1double == 0 || operando2double == 0) {
                return "Error: no se puede dividir por 0";
            } else {
                return "El resultado de la división es: " + (operando1double / operando2double);
            }
        } catch(NumberFormatException e) {
            return ERROR_CONVERSION;
        }
    }


    // Método que gestiona el cliente entrante
    public static void gestionarCliente(Socket socket) {
        try(
                // Crear canales de comunicación para leer y devolver información
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
        ) {
            // Procesar los comandos que nos lleguen mediante un 'switch'
            String comando;
            String respuesta;
            String[] comandoTrozeado;
            while((comando = bufferedReader.readLine()) != null) {
                // Trozear la línea recibida por parte del cliente para obtener el comando y los operadores
                comandoTrozeado = comando.split("\\s+", 3);

                // Si la línea no contiene 3 palabras ni accedemos al switch porque las operaciones siempre van a necesitar 3 palabras. Además, así podemos tener un error personalizado para esto
                if(comandoTrozeado[0].trim().equalsIgnoreCase("fin")) {
                    respuesta = "¡Hasta la próxima!";
                } else if(comandoTrozeado.length == 3) {
                    // Dependiendo del comando que el servidor reciba se utilizará un método u otro
                    respuesta = switch(comandoTrozeado[0].trim().toLowerCase()) {
                        case "sum" -> sumar(comandoTrozeado[1], comandoTrozeado[2]);
                        case "res" -> restar(comandoTrozeado[1], comandoTrozeado[2]);
                        case "mul" -> multiplicar(comandoTrozeado[1], comandoTrozeado[2]);
                        case "div" -> dividir(comandoTrozeado[1], comandoTrozeado[2]);
                        case "fin" -> "Saliendo del servidor...";
                        default -> "Comando introducido desconocido, introduce otro comando";
                    };
                } else {
                    respuesta = "Error: debes introducir los comandos en este formatos: OPERACION <operando1> <operando2> o FINAL";
                }
                printWriter.println(respuesta);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("El cliente " + Thread.currentThread().getName() + " ha dejado de utilizar la calculadora");
    }

    // Puerto por el que el servidor escuchará peticiones
    private static final int PORT = 5555;

    // --- Iniciación del servidor y creación de 'sockets' mediante hilos para escuchar múltiples peticiones a la vez
    public static void main(String[] args) {
        // Creación del 'socket' del servidor
        try(ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor escuchando por el puerto: " + PORT + "...");
            while(true) {
                Socket socket = serverSocket.accept();
                Thread thread = new Thread(() -> gestionarCliente(socket));
                thread.start();
                System.out.println("Cliente conectado: " + thread.getName());
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }
}

