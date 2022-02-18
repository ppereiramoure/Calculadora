package calculadora.servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * Clase Servidor que iniciara la escucha por un
 * puerto y recibira los numeros y operaciones de calculadora
 * Uso Thread para comunicacion asincrona
 * De forma que el servidor permita varias conexiones
 */
public class Servidor extends Thread {

    private int puerto; // Número de puerto para levantar el server
    private DataInputStream entradaDatos; //Canal de entrada de datos de clientes
    ArrayList <String> numeros = new ArrayList();
    ArrayList  <String> ultimaOperacion = new ArrayList();
    private Double resultado = 0.0;

    /**
     * Constructor
     * @param puerto: Numero de puerto de escucha para la conexion de cliente por Socket
     */
    public Servidor(int puerto) {
        this.puerto = puerto;
    }

    //Sobreescribimos método run para que el servidor se inicie y permita
    //Hilo por conexión
    /**
     * Metodo run ejecutar hilos
     */

    @Override
    public void run() {
        try {
            //Creamos instancia de ServerSocket para recibir peticion de cliente
            ServerSocket socketServidor = new ServerSocket(this.puerto);
            System.out.println(" Servidor en espera por puerto " + puerto);
            //Dejaremos el servidor siempre escuchando
            //En este caso bucle infinito
            String mensajeCliente = "";
            while(!mensajeCliente.equalsIgnoreCase("Bucle infinito")) {
                // aceptamos la conexión
                Socket socketCliente = socketServidor.accept();
                //Estableceremos canal de comunicación con cliente
                entradaDatos = new DataInputStream(socketCliente.getInputStream());
                //Leemos mensaje del cliente
                mensajeCliente = entradaDatos.readUTF();
                String[] partes = mensajeCliente.split(",");

                //Capturamos los números y operaciones que llegan del cliente
                numeros.add(partes[0]);
                ultimaOperacion.add(partes[1]);
                //Evaluamos en función de operando
                if (numeros.size() == 2 && ultimaOperacion.get(1).equals("=")) {
                    switch (ultimaOperacion.get(0)) {
                        case "+":
                            resultado = Double.parseDouble(numeros.get(0)) + Double.parseDouble(numeros.get(1));
                            break;
                        case "-":
                            resultado = Double.parseDouble(numeros.get(0)) - Double.parseDouble(numeros.get(1));
                            break;
                        case "*":
                            resultado = Double.parseDouble(numeros.get(0)) * Double.parseDouble(numeros.get(1));
                            break;
                        case "/":
                            resultado = Double.parseDouble(numeros.get(0)) / Double.parseDouble(numeros.get(1));
                            break;
                    }
                    // Limpiamos los Arrays
                    numeros.clear();
                    ultimaOperacion.clear();

                    //Enviamos datos al Cliente
                    OutputStream auxOut = socketCliente.getOutputStream();
                    DataOutputStream infoSalida = new DataOutputStream(auxOut);
                    infoSalida.writeUTF(String.valueOf(resultado));
                    System.out.println(resultado);
                }
            }
        } catch (IOException e) {
            System.out.println(" Error en puerto " + puerto + e.getMessage());
            System.exit(2); //Cerramos aplicación con código de salida 2 Standar Excp
        }
    }
}