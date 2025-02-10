/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Matriz;

/**
 *
 * @author Alexis
 */
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;

class MatrixSumTask extends RecursiveTask<Integer> {
    private final int[][] matrix;
    private final int start;
    private final int end;
    private int THRESHOLD = 4000;
    //public static int hilos = 0;

    public MatrixSumTask(int[][] matrix, int start, int end, int umbral) {
        this.matrix = matrix;
        this.start = start;
        this.end = end;
        this.THRESHOLD = umbral;
    }

    @Override
    protected Integer compute() {
        if ((end - start) < THRESHOLD) {
            //hilos++;
            //System.out.println(MatrixSumTask.hilos);
            int sum = 0;
            for (int i = start; i < end; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    sum += matrix[i][j];
                }
            }
            return sum;
        } else {
            int mid = (start + end) / 2;
            MatrixSumTask task1 = new MatrixSumTask(matrix, start, mid, THRESHOLD);
            MatrixSumTask task2 = new MatrixSumTask(matrix, mid, end, THRESHOLD);
            task1.fork();
            int result2 = task2.compute();
            int result1 = task1.join();
            return result1 + result2;
        }
    }
}

public class MatrizParaleloSuma {
    
    private int size = 4000;
    private int sum = 0;
    private int[][] matrix;
    
    public MatrizParaleloSuma(int size){
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
        System.out.println("Tiempo de ejecución Paralelo: " + (endTime - startTime) + " ms");
        return endTime - startTime;
    }
    
    public void suma(){
        
        ForkJoinPool pool = new ForkJoinPool();  
        int umbral = 0;
        switch(size){ // Limitar a 4 hilos
            case 10000,15000 -> {umbral = 4000;}
            case 20000 -> {umbral = 6000;}
        }
        MatrixSumTask task = new MatrixSumTask(matrix, 0, matrix.length, umbral);
        int sum = pool.invoke(task);
        this.sum = sum;
    }
    
    public static void main(String[] args) {

        MatrizParaleloSuma par = new MatrizParaleloSuma(20000);
        long num = par.computeSum();
        System.out.println("num = " + num);
    }
}
