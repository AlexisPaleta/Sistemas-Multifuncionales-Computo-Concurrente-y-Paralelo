/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GameOfLife;

/**
 *
 * @author Alexis
 */
public class JuegoDeLaVida {
    
    private final int rows = 100;
    private final int cols = 100;
    private final boolean[][] currentGeneration = new boolean[rows][cols];
    
    public JuegoDeLaVida(){
        initializePattern();
    }
    
    
    private void initializePattern() {
        // Ejemplo: Patrón "glider"
        currentGeneration[1][0] = true;
        currentGeneration[2][1] = true;
        currentGeneration[0][2] = true;
        currentGeneration[1][2] = true;
        currentGeneration[2][2] = true;
    }

    public boolean[][] calculateNextGeneration() {
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

    private int countAliveNeighbors(int row, int col) {
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
