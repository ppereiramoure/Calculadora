package calculadora.servidor;


public class Main {

    public static void main(String[] args) {
        int puerto = 8888;
            Servidor servidor = new Servidor(puerto);

            servidor.start();
        }
    }
