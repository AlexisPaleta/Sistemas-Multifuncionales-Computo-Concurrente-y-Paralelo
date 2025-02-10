/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CalculoPi;

/**
 *
 * @author Alexis
 */

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.*;

public class PiCalculatorParalelo {
    private static final BigDecimal FOUR = BigDecimal.valueOf(4);
    private static final int SCALE = 5000; // Digits of precision
    

    // Método arctan usado en todas las versiones
    public static BigDecimal arctan(int inverseX, int scale) {
        BigDecimal result, numer, term;
        BigDecimal invX = BigDecimal.valueOf(inverseX);
        BigDecimal invX2 = BigDecimal.valueOf(inverseX * inverseX);
        numer = BigDecimal.ONE.divide(invX, scale, RoundingMode.HALF_EVEN);
        result = numer;
        int i = 1;
        do {
            numer = numer.divide(invX2, scale, RoundingMode.HALF_EVEN);
            int denom = 2 * i + 1;
            term = numer.divide(BigDecimal.valueOf(denom), scale, RoundingMode.HALF_EVEN);
            if ((i % 2) != 0)
                result = result.subtract(term);
            else
                result = result.add(term);
            i++;
        } while (term.compareTo(BigDecimal.ZERO) != 0);
        return result;
    }

    // Versión secuencial
    public static BigDecimal computePiSequential(int size) {
        BigDecimal arctan1_5 = arctan(5, size);
        BigDecimal arctan1_239 = arctan(239, size);
        return arctan1_5.multiply(FOUR).subtract(arctan1_239).multiply(FOUR)
                .setScale(size - 5, RoundingMode.HALF_UP);
    }

    // Versión concurrente
    public static BigDecimal computePiConcurrent(int size) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Callable<BigDecimal> task1 = () -> arctan(5, size);
        Callable<BigDecimal> task2 = () -> arctan(239, size);

        Future<BigDecimal> future1 = executor.submit(task1);
        Future<BigDecimal> future2 = executor.submit(task2);

        BigDecimal arctan1_5 = future1.get();
        BigDecimal arctan1_239 = future2.get();

        executor.shutdown();

        return arctan1_5.multiply(FOUR).subtract(arctan1_239).multiply(FOUR)
                .setScale(size - 5, RoundingMode.HALF_UP);
    }

    // Versión paralela usando ForkJoinPool
    public static BigDecimal computePiParallel(int size) {
        ForkJoinPool pool = new ForkJoinPool();

        ArctanTask task1 = new ArctanTask(5, size);
        ArctanTask task2 = new ArctanTask(239, size);

        BigDecimal arctan1_5 = pool.invoke(task1);
        BigDecimal arctan1_239 = pool.invoke(task2);

        pool.shutdown();

        return arctan1_5.multiply(FOUR).subtract(arctan1_239).multiply(FOUR)
                .setScale(size - 5, RoundingMode.HALF_UP);
    }

    // Tarea para calcular arctan usando ForkJoin
    private static class ArctanTask extends RecursiveTask<BigDecimal> {
        private final int inverseX;
        private final int scale;

        ArctanTask(int inverseX, int scale) {
            this.inverseX = inverseX;
            this.scale = scale;
        }

        @Override
        protected BigDecimal compute() {
            return arctan(inverseX, scale);
        }
    }

//    public static void main(String[] args) throws InterruptedException, ExecutionException {
//        long start, end;
//
//        // Ejecución secuencial
//        start = System.currentTimeMillis();
//        BigDecimal piSequential = computePiSequential();
//        end = System.currentTimeMillis();
//        System.out.println("Pi (Secuencial): " + piSequential);
//        System.out.println("Tiempo (Secuencial): " + (end - start) + " ms");
//
//        // Ejecución concurrente
//        start = System.currentTimeMillis();
//        BigDecimal piConcurrent = computePiConcurrent();
//        end = System.currentTimeMillis();
//        System.out.println("Pi (Concurrente): " + piConcurrent);
//        System.out.println("Tiempo (Concurrente): " + (end - start) + " ms");
//
//        // Ejecución paralela
//        start = System.currentTimeMillis();
//        BigDecimal piParallel = computePiParallel();
//        end = System.currentTimeMillis();
//        System.out.println("Pi (Paralelo): " + piParallel);
//        System.out.println("Tiempo (Paralelo): " + (end - start) + " ms");
//    }
}


