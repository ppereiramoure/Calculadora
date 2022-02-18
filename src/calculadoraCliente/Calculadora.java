package calculadoraCliente;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;


public class Calculadora {

    private static final String DIRECCION = "localhost";
    private static final int PUERTO = 8888;
    private static Double resultado;
    private JPanel Panel;
    private JTextField Pantalla;
    private JButton Boton8;
    private JButton BotonDiv;
    private JButton Boton7;
    private JButton Boton9;
    private JButton Boton4;
    private JButton Boton5;
    private JButton Boton6;
    private JButton BotonMul;
    private JButton Boton1;
    private JButton Boton2;
    private JButton Boton3;
    private JButton BotonResta;
    private JButton Boton0;
    private JButton BotonPunto;
    private JButton BotonIgual;
    private JButton BotonSuma;
    private JButton BotonAC;
    private JButton BotonOFF;
    private boolean primerNumero;

    public Calculadora() {
        primerNumero = true;
        ActionListener escucha = new insertarNumerosPantalla();
        ActionListener operador = new Operaciones();
        ponerEscuchaBoton(escucha, Boton0);
        ponerEscuchaBoton(escucha, Boton1);
        ponerEscuchaBoton(escucha, Boton2);
        ponerEscuchaBoton(escucha, Boton3);
        ponerEscuchaBoton(escucha, Boton4);
        ponerEscuchaBoton(escucha, Boton5);
        ponerEscuchaBoton(escucha, Boton6);
        ponerEscuchaBoton(escucha, Boton7);
        ponerEscuchaBoton(escucha, Boton8);
        ponerEscuchaBoton(escucha, Boton9);
        ponerEscuchaBoton(operador, BotonDiv);
        ponerEscuchaBoton(operador, BotonMul);
        ponerEscuchaBoton(operador, BotonResta);
        ponerEscuchaBoton(operador, BotonIgual);
        ponerEscuchaBoton(operador, BotonSuma);


        BotonAC.addActionListener(actionEvent -> {
            Pantalla.setText(null);
            Pantalla.setText("0");
            resultado = 0.0;
            primerNumero = true;
        });
        BotonOFF.addActionListener(actionEvent -> {
            try {
                apagarCalculadora();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });
        BotonPunto.addActionListener(actionEvent -> {
            String texto = Pantalla.getText();
            if (texto.length() <= 1 && texto.equals("0")) {
                Pantalla.setText(Pantalla.getText().concat("."));
                primerNumero = false;
            } else {
                if (!validarPunto(Pantalla.getText())) {
                    Pantalla.setText(Pantalla.getText().concat("."));

                }
            }


        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Calculadora");
        frame.setContentPane(new Calculadora().Panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }


    public void ponerEscuchaBoton(ActionListener escucha, JButton boton) {
        boton.addActionListener(escucha);
    }

    public void apagarCalculadora() throws InterruptedException {
        int segundos = 3;

        while (segundos != 0) {
            Thread.sleep(1000);
            segundos--;

        }

        if (segundos == 0) {
            System.exit(0);
        }
    }


    private void createUIComponents() {
        Pantalla = new JTextField("0");
    }


    public boolean validarPunto(String textoLabel) {

        boolean validacion = false;

        for (int i = 0; i < textoLabel.length(); i++) {
            if (textoLabel.charAt(i) == '.') {
                validacion = true;
                break;
            }
        }
        return validacion;
    }


    class insertarNumerosPantalla implements ActionListener {

        private String entrada;

        @Override
        public void actionPerformed(ActionEvent actionEvent) {

            entrada = actionEvent.getActionCommand();
            if (primerNumero) {
                Pantalla.setText("");
                primerNumero = false;
            }
            Pantalla.setText(Pantalla.getText().concat(entrada));
        }
    }


    class Operaciones extends Thread implements ActionListener {

        private static String datoPantalla;
        private String ultimaOperacion = "=";
        private double datoConvertido;
        private ConectarServer conexServer;


        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            Thread hilo = new Thread(new Thread(() -> {
                try {
                    String operacion = actionEvent.getActionCommand();
                    conexServer = new ConectarServer();
                    datoConvertido = Double.parseDouble(Pantalla.getText());
                    datoPantalla = String.valueOf(datoConvertido);
                    conexServer.enviarNumerosServidor(datoPantalla.concat(",").concat(operacion));
                    ultimaOperacion = operacion;
                } finally {
                    try {
                        conexServer.cerrarConexion();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }));
            hilo.start();
            primerNumero = true;
        }

        public void acumularOperacion(Double numero, String operacio) {

        }

    }


    class ConectarServer extends Thread {

        static boolean infintoC = true;
        static Socket skCliente;


        public synchronized void enviarNumerosServidor(String numero) {
            try {
                skCliente = new Socket(DIRECCION, PUERTO);
                while (infintoC) {
                    if ("SALIR".equalsIgnoreCase(numero)) {
                        infintoC = false;
                        numero = "salir";
                    } else {
                        OutputStream auxOut = skCliente.getOutputStream();
                        DataOutputStream infoSalida = new DataOutputStream(auxOut);
                        infoSalida.writeUTF(numero);
                        recibirDatosServer();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public void recibirDatosServer() throws IOException {

            InputStream auxIn = skCliente.getInputStream();
            DataInputStream infoEntrada = new DataInputStream(auxIn);
            String lectura = infoEntrada.readUTF();
            System.out.println(lectura);
            Pantalla.setText(lectura);
        }


        public void cerrarConexion() throws IOException {
            skCliente.close();
        }
    }
}

