/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Matriz;

/**
 *
 * @author Alexis
 */
class MatrixSumThread extends Thread {
    private final int[][] matrix;
    private final int start;
    private final int end;
    private int result = 0;

    public MatrixSumThread(int[][] matrix, int start, int end) {
        this.matrix = matrix;
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {
        for (int i = start; i < end; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                result += matrix[i][j];
            }
        }
    }

    public int getResult() {
        return result;
    }
}

public class MatrizConcurrenteSuma {
    
    private int size = 4000;
    private int sum = 0;
    private int[][] matrix;
    
    public MatrizConcurrenteSuma(int size){
        this.size = size;
        matrix = new int[size][size];
        
        // Llenar la matriz con valores
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = 1;
            }
        }
    }
    
    public long computeSum(){
        long startTime = System.currentTimeMillis();
        suma();
        long endTime = System.currentTimeMillis();
        System.out.println("Suma total: " + sum);
        System.out.println("size = " + size);
        System.out.println("Tiempo de ejecución Concurrente: " + (endTime - startTime) + " ms");
        return endTime - startTime;
    }
    
    public void suma(){
         
        // Crear hilos (ej. 4 hilos)
        int numThreads = 4;
        MatrixSumThread[] threads = new MatrixSumThread[numThreads];
        int step = size / numThreads;

        // Iniciar cada hilo para calcular una parte de la matriz
        for (int i = 0; i < numThreads; i++) {
            int start = i * step;
            int end = (i == numThreads - 1) ? size : (i + 1) * step;
            threads[i] = new MatrixSumThread(matrix, start, end);
            threads[i].start();
        }

        // Esperar a que todos los hilos terminen
        int totalSum = 0;
        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException ex) {
                System.err.println("hubo un error al realizar la creacion de los hilos en la suma concurrente");
            }
            totalSum += threads[i].getResult();
        }
        this.sum = totalSum;
    }
    
    public static void main(String[] args) throws InterruptedException {
        MatrizConcurrenteSuma par = new MatrizConcurrenteSuma(20000);
        long num = par.computeSum();
        System.out.println("num = " + num);
    }
}
