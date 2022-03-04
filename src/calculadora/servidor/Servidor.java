package calculadora.servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Servidor extends Thread {

    ArrayList<String> numeros = new ArrayList();
    ArrayList<String> operacionFinal = new ArrayList();
    private final int puerto;
    private DataInputStream entradaDatos;
    private Double resultado = 0.0;


    public Servidor(int puerto) {
        this.puerto = puerto;
    }


    @Override
    public void run() {
        try {
            ServerSocket socketServidor = new ServerSocket(this.puerto);
            System.out.println(" Servidor esperando en el puerto " + puerto);
            String mensajeCliente = "";
            while (!mensajeCliente.equalsIgnoreCase("Bucle")) {
                Socket socketCliente = socketServidor.accept();
                entradaDatos = new DataInputStream(socketCliente.getInputStream());
                mensajeCliente = entradaDatos.readUTF();
                String[] partes = mensajeCliente.split(",");

                numeros.add(partes[0]);
                operacionFinal.add(partes[1]);
                if (numeros.size() == 2 && operacionFinal.get(1).equals("=")) {
                    switch (operacionFinal.get(0)) {
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
                    numeros.clear();
                    operacionFinal.clear();

                    OutputStream auxOut = socketCliente.getOutputStream();
                    DataOutputStream infoSalida = new DataOutputStream(auxOut);
                    infoSalida.writeUTF(String.valueOf(resultado));
                    System.out.println(resultado);
                }
            }
        } catch (IOException e) {
            System.out.println(" Error en puerto " + puerto + e.getMessage());
            System.exit(2);
        }
    }
}