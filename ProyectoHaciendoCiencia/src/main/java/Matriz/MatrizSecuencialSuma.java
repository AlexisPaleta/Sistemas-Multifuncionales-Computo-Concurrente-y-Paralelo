/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Matriz;

/**
 *
 * @author Alexis
 */
public class MatrizSecuencialSuma {
    
    
    private int size = 4000;
    private int sum = 0;
    private int[][] matrix;
    
    public MatrizSecuencialSuma(int size){
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
        System.out.println("Tiempo de ejecución Secuencial: " + (endTime - startTime) + " ms");
        return endTime - startTime;
    }
    
    public void suma() {
        // Sumar todos los valores de la matriz de forma secuencial
        int sum = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                sum += matrix[i][j];
            }
        }
        this.sum = sum;
    }
    
    public static void main(String[] args) {

        MatrizSecuencialSuma par = new MatrizSecuencialSuma(20000);
        long num = par.computeSum();
        System.out.println("num = " + num);
    }
}
