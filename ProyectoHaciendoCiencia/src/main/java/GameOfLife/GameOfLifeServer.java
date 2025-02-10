/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GameOfLife;

/**
 *
 * @author Alexis
 */
import java.io.*;
import java.net.*;

public class GameOfLifeServer {
    private static final int PORT = 12346;
    private static final int rows = 100;
    private static final int cols = 100;
    private static final boolean[][] currentGeneration = new boolean[rows][cols];

    public static void main(String[] args) {
        // Inicializa la matriz con un patrón (ejemplo: glider)
        initializePattern();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor escuchando en el puerto " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente: " + clientSocket.getInetAddress() + " conectado al juego de la vida");

                // Manejar el cliente en un hilo separado
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void iniciar(){
        // Inicializa la matriz con un patrón (ejemplo: glider)
        initializePattern();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor escuchando en el puerto " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente: " + clientSocket.getInetAddress() + " conectado al juego de la vida");

                // Manejar el cliente en un hilo separado
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.out.println("Servidor juego vida terminado");
        }
    }

    private static void handleClient(Socket clientSocket) {
        initializePattern();
        try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

            while (true) {
                // Esperar a recibir la solicitud del cliente
                String request = (String) in.readObject();
                if ("GET_NEXT_GENERATION".equals(request)) {
                    boolean[][] nextGeneration = calculateNextGeneration();
                    out.writeObject(nextGeneration);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("El cliente: " + " se fue del juego de la vida");
        }
    }

    private static void initializePattern() {
        // Ejemplo: Patrón "glider"
        currentGeneration[1][0] = true;
        currentGeneration[2][1] = true;
        currentGeneration[0][2] = true;
        currentGeneration[1][2] = true;
        currentGeneration[2][2] = true;
    }

    private static boolean[][] calculateNextGeneration() {
        boolean[][] nextGeneration = new boolean[rows][cols];
        int threads = 4; // Número de hilos a utilizar
        Thread[] threadPool = new Thread[threads];

        // Divide el trabajo entre hilos
        for (int i = 0; i < threads; i++) {
            final int startRow = i * (rows / threads);
            final int endRow = (i + 1) * (rows / threads);
            threadPool[i] = new Thread(() -> {
                for (int r = startRow; r < endRow; r++) {
                    for (int c = 0; c < cols; c++) {
                        int aliveNeighbors = countAliveNeighbors(r, c);
                        nextGeneration[r][c] = currentGeneration[r][c] ?
                                (aliveNeighbors == 2 || aliveNeighbors == 3) : (aliveNeighbors == 3);
                    }
                }
            });
            threadPool[i].start();
        }

        // Espera a que todos los hilos terminen
        for (Thread thread : threadPool) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Actualiza la generación actual
        for (int r = 0; r < rows; r++) {
            System.arraycopy(nextGeneration[r], 0, currentGeneration[r], 0, cols);
        }

        return nextGeneration;
    }

    private static int countAliveNeighbors(int row, int col) {
        int count = 0;
        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                if (r >= 0 && r < rows && c >= 0 && c < cols && (r != row || c != col)) {
                    count += currentGeneration[r][c] ? 1 : 0;
                }
            }
        }
        return count;
    }
}


