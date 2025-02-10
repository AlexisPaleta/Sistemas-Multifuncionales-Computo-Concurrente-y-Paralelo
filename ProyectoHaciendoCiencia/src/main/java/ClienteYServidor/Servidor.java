/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ClienteYServidor;

import BaseDeDatos.Alumno;
import BaseDeDatos.AlumnoDaoJDBC;
import static CalculoPi.PiCalculatorParalelo.*;
import GameOfLife.*;
import Mandelbrot.*;
import Matriz.*;
import java.io.*;
import java.math.BigDecimal;
import java.net.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 *
 * @author Alexis
 */
public class Servidor {
    private static final int PORT = 12345;

    public static void main(String[] args) { //Apenas se inicia el servidor se crea un socket server
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor en espera de conexiones...");
            // Manejar el cliente en un hilo separado
            new Thread(() -> vida()).start(); //Tiene que crearse en un hilo diferente porque sino
            //el solo existira un unico hilo, y ese se iria a la logica del servidor del juego de la vida
            //que tiene un ciclo infinito, en resumen, nunca saldria de ahi
            while (true) { //hasta que no se detenga la ejecución del programa no se dejará de escuchar a
                //clientes
                Socket clientSocket = serverSocket.accept(); 
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());
                // Crear un nuevo hilo para manejar la conexión de cada cliente
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.out.println("Hubo un error al tratar crear el servidor y escuchar clientes");
        }
    }
    
    public static void vida(){
        GameOfLifeServer sv = new GameOfLifeServer();
        sv.iniciar();
    }
}

// Clase para manejar cada conexión de cliente en un hilo separado
class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {//Cuando se inicialice el hilo del cliente se va a ejecutar el metodo run
        //dentro de este se recuperan los objetos que permiten la comunicacion entre el cliente y el servidor,
        //ya que el cliente va a estar enviando mensajes, y el servidor envia respuestas
        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            String request = in.readLine(); //Se lee la peticion del cliente
            System.out.println("request = " + request);

            if (request != null) { //Des pues de que se reciba la peticion y se vea que no es nula, se pasa al
                //la peticion recibida al metodo que se encarga de la lógica de que hacer con cada
                //petición, según la petición que sea es el método que después se va a ejecutar
                handleRequest(request, out);
            }

        } catch (IOException e) {
            System.out.println("Error al tratar de establecer la comunicación con el cliente: " + clientSocket.getInetAddress());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Comunicacion cerrada con el cliente: " + clientSocket.getInetAddress());
            } catch (IOException e) {
                System.out.println("Error al tratar de finalizar la conexion con el clinete");
            }
        }
    }

    private void handleRequest(String request, PrintWriter out) {//En este método se controla que hacer 
        //según la petición que se reciba, en base a eso se ejecuta un método específico
        switch (request) {
            case "mandelbrot_concurrente":
                out.println("Ejecutando Mandelbrot Concurrente...");
                runMandelbrotConcurrente(out);
                break;
            case "mandelbrot_paralelo":
                out.println("Ejecutando Mandelbrot Paralelo...");
                runMandelbrotParallel(out);
                break;
            case "mandelbrot_secuencial":
                out.println("Ejecutando Mandelbrot Secuencial...");
                runMandelbrotSecuencial(out);
                break;
            case "juego_de_la_vida":
                System.out.println("Ejecutando Juego de la vida...");
                break;
            case "lista_alumnos":
                out.println("Ejecutando Listado de alumnos...");
                enviarListaAlumnos(out);
                break;
            default:
                if(request.startsWith("matriz")){
                    out.println("Ejecutando Operaciones con Matrices...");
                    runOperacionesMatrices(request, out);
                }else{ //Se ejecutan las operaciones de Pi
                    out.println("Ejecutando Calculo de digitos de Pi...");
                    runOperacionesPi(request, out);
                }
                
                break;
        }
    }
    
    private void runOperacionesMatrices(String request, PrintWriter out){
        if(request.startsWith("matriz_paralelo")){
            runMatrizParalelo(request, out);
        }else if(request.startsWith("matriz_concurrente")){
            runMatrizConcurrente(request, out);
        }else{
            runMatrizSecuencial(request, out);
        }
    }
    
    private void runOperacionesPi(String request, PrintWriter out){
        if(request.startsWith("pi_paralelo")){
            runPiParalelo(request, out);
        }else if(request.startsWith("pi_concurrente")){
            runPiConcurrente(request, out);
        }else{
            runPiSecuencial(request, out);
        }
    }
    
    private void runPiSecuencial(String request, PrintWriter out) {
        int size = 0;
        if(request.endsWith("1000")){
            size = 1000;
        }else if(request.endsWith("5000")){
            size = 5000;
        }else{
            size = 10000;
        }
        
        long start, end;
        start = System.currentTimeMillis();
        BigDecimal piSequential = computePiSequential(size);
        end = System.currentTimeMillis();
        long executionTime = end - start; //Se recibe el tiempo que tardó en crearse 
        //la imagen de mandelbrot
        
        out.println("META:" + request + "," + executionTime); 
    }
    
    private void runPiConcurrente(String request, PrintWriter out) {
        int size = 0;
        if(request.endsWith("1000")){
            size = 1000;
        }else if(request.endsWith("5000")){
            size = 5000;
        }else{
            size = 10000;
        }
        
        long start, end;
        start = System.currentTimeMillis();
        try {
            BigDecimal piSequential = computePiConcurrent(size);
        } catch (InterruptedException ex) {
            System.out.println("Hubo un error al calcular Pi Concurr");
        } catch (ExecutionException ex) {
            System.out.println("Hubo un error al calcular Pi Concurrente");
        }
        end = System.currentTimeMillis();
        long executionTime = end -  start; //Se recibe el tiempo que tardó en crearse 
        //la imagen de mandelbrot
        
        out.println("META:" + request + "," + executionTime); 
    }
    
    private void runPiParalelo(String request, PrintWriter out) {
        int size = 0;
        if(request.endsWith("1000")){
            size = 1000;
        }else if(request.endsWith("5000")){
            size = 5000;
        }else{
            size = 10000;
        }
        
        long start, end;
        start = System.currentTimeMillis();
        BigDecimal piSequential = computePiParallel(size);
        end = System.currentTimeMillis();
        long executionTime = end -  start; //Se recibe el tiempo que tardó en crearse 
        //la imagen de mandelbrot
        
        out.println("META:" + request + "," + executionTime); 
    }
    
    
    
    private void runMandelbrotConcurrente(PrintWriter out) {
        MandelbrotConcurrente mandelbrot = new MandelbrotConcurrente(); //Se crea un objeto de la clase de mandelbrot
        // en su version paralela
        long executionTime = mandelbrot.computeMandelbrot(); //Se recibe el tiempo que tardó en crearse 
        //la imagen de mandelbrot
        try {
            byte[] imageBytes = mandelbrot.getImageBytes(); // Método que obtenga la imagen en bytes
            //Se envia el nombre del método que se ejecutó, con el tiempo que demoró y el largo de la imagen, el largo se
            //necesita enviar para que la imagen pueda ser recibida por el cliente, ya que se recibe en un
            //arreglo de bytes, y para los arreglos, en su declaración se necesita saber el tamaño de estos
            out.println("META:mandelbrot_concurrente," + executionTime + "," + imageBytes.length);
            
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String clientAck = in.readLine(); // Espera confirmación del cliente, porque sino la imagen
            //se podria estar enviando cuando el cliente no este listo
            
            // Envía la imagen en bytes después de la línea anterior
            if ("ACK_META".equals(clientAck)) { //Se envia solo cuando el cliente envió el mensaje de que
                //estaba listo para recibir la imagen
                OutputStream clientOutput = clientSocket.getOutputStream();
                clientOutput.write(imageBytes);
                clientOutput.flush();
            }
        } catch (IOException e) {
            System.out.println("Hubo un error al realizar Mandelbrot Concurrente");
        }
    }

    private void runMandelbrotParallel(PrintWriter out) {
        MandelbrotParalelo mandelbrot = new MandelbrotParalelo(); //Se crea un objeto de la clase de mandelbrot
        // en su version paralela
        long executionTime = mandelbrot.computeMandelbrot(); //Se recibe el tiempo que tardó en crearse 
        //la imagen de mandelbrot
        try {
            byte[] imageBytes = mandelbrot.getImageBytes(); // Método que obtenga la imagen en bytes
            //Se envia el nombre del método que se ejecutó, con el tiempo que demoró y el largo de la imagen, el largo se
            //necesita enviar para que la imagen pueda ser recibida por el cliente, ya que se recibe en un
            //arreglo de bytes, y para los arreglos, en su declaración se necesita saber el tamaño de estos
            out.println("META:mandelbrot_paralelo," + executionTime + "," + imageBytes.length);
            
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String clientAck = in.readLine(); // Espera confirmación del cliente, porque sino la imagen
            //se podria estar enviando cuando el cliente no este listo
            
            // Envía la imagen en bytes después de la línea anterior
            if ("ACK_META".equals(clientAck)) { //Se envia solo cuando el cliente envió el mensaje de que
                //estaba listo para recibir la imagen
                OutputStream clientOutput = clientSocket.getOutputStream();
                clientOutput.write(imageBytes);
                clientOutput.flush();
            }
        } catch (IOException e) {
            System.out.println("Hubo un error al realizar Mandelbrot Paralelo");
        }
    }
    
    
    private void runMandelbrotSecuencial(PrintWriter out) {
        MandelbrotSecuencial mandelbrot = new MandelbrotSecuencial(); //Se crea un objeto de la clase de mandelbrot
        // en su version paralela
        long executionTime = mandelbrot.computeMandelbrot(); //Se recibe el tiempo que tardó en crearse 
        //la imagen de mandelbrot
        try {
            byte[] imageBytes = mandelbrot.getImageBytes(); // Método que obtenga la imagen en bytes
            //Se envia el nombre del método que se ejecutó, con el tiempo que demoró y el largo de la imagen, el largo se
            //necesita enviar para que la imagen pueda ser recibida por el cliente, ya que se recibe en un
            //arreglo de bytes, y para los arreglos, en su declaración se necesita saber el tamaño de estos
            out.println("META:mandelbrot_secuencial," + executionTime + "," + imageBytes.length);
            
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String clientAck = in.readLine(); // Espera confirmación del cliente, porque sino la imagen
            //se podria estar enviando cuando el cliente no este listo
            
            // Envía la imagen en bytes después de la línea anterior
            if ("ACK_META".equals(clientAck)) { //Se envia solo cuando el cliente envió el mensaje de que
                //estaba listo para recibir la imagen
                OutputStream clientOutput = clientSocket.getOutputStream();
                clientOutput.write(imageBytes);
                clientOutput.flush();
            }
        } catch (IOException e) {
            System.out.println("Hubo un error al realizar Mandelbrot Secuencial");
        }
    }
    
    private void runMatrizConcurrente(String request, PrintWriter out) {
        int size = 0;
        if(request.endsWith("10000")){
            size = 10000;
        }else if(request.endsWith("15000")){
            size = 15000;
        }else{
            size = 20000;
        }
        MatrizConcurrenteSuma matriz = new MatrizConcurrenteSuma(size); //Se crea un objeto de la clase de mandelbrot
        // en su version paralela
        long executionTime = matriz.computeSum(); //Se recibe el tiempo que tardó en crearse 
        //la imagen de mandelbrot
        
        out.println("META:" + request + "," + executionTime); 
    }
    
    private void runMatrizParalelo(String request, PrintWriter out) {
        int size = 0;
        if(request.endsWith("10000")){
            size = 10000;
        }else if(request.endsWith("15000")){
            size = 15000;
        }else{
            size = 20000;
        }
        MatrizParaleloSuma matriz = new MatrizParaleloSuma(size); //Se crea un objeto de la clase de mandelbrot
        // en su version paralela
        long executionTime = matriz.computeSum(); //Se recibe el tiempo que tardó en crearse 
        //la imagen de mandelbrot
        
        out.println("META:" + request + "," + executionTime); 
    }
    
    private void runMatrizSecuencial(String request, PrintWriter out) {
        int size = 0;
        if(request.endsWith("10000")){
            size = 10000;
        }else if(request.endsWith("15000")){
            size = 15000;
        }else{
            size = 20000;
        }
        MatrizSecuencialSuma matriz = new MatrizSecuencialSuma(size); //Se crea un objeto de la clase de mandelbrot
        // en su version paralela
        long executionTime = matriz.computeSum(); //Se recibe el tiempo que tardó en crearse 
        //la imagen de mandelbrot
        
        out.println("META:" + request + "," + executionTime); 
    }
    
    private void runGameOfLife(){
        JuegoDeLaVida GOL = new JuegoDeLaVida();
        
        try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

            while (true) {
                // Esperar a recibir la solicitud del cliente
                String request = (String) in.readObject();
                if ("GET_NEXT_GENERATION".equals(request)) {
                    boolean[][] nextGeneration = GOL.calculateNextGeneration();
                    // Asegúrate de que nextGeneration no sea null
                    if (nextGeneration != null) {
                        out.writeObject(nextGeneration);
                        out.flush(); // Asegúrate de hacer flush después de enviar
                    } else {
                        System.out.println("La siguiente generación es nula");
                    }
                }else{
                    System.out.println("Fin juego de la vida");
                    return;
                }
                
            }
        } catch ( ClassNotFoundException e) {
            System.out.println("Error en el juego de la vida");
        } catch (IOException ex) {
            System.out.println("Error al tratar de enviar la generacion");
        }
    }
    
    private void enviarListaAlumnos(PrintWriter out) {
        AlumnoDaoJDBC alumnoDao = new AlumnoDaoJDBC();
        List<Alumno> alumnos = alumnoDao.listaClientes();

        // Enviar la cabecera
        out.println("META:lista_alumnos," + alumnos.size());

        // Enviar los datos de los alumnos
        for (Alumno alumno : alumnos) {
            out.println(alumno.getMatricula() + "," + alumno.getNombre() + "," + alumno.getPromedio());
        }
    }
    
    

}