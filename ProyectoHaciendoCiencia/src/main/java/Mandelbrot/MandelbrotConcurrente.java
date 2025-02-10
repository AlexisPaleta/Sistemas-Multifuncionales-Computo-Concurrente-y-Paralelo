/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Mandelbrot;

/**
 *
 * @author Alexis
 */
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

class MandelbrotTask extends Thread {
    private final int startX, endX, width, height, maxIter;
    private final BufferedImage image;

    public MandelbrotTask(int startX, int endX, int width, int height, int maxIter, BufferedImage image) {
        this.startX = startX;
        this.endX = endX;
        this.width = width;
        this.height = height;
        this.maxIter = maxIter;
        this.image = image;
    }

    @Override
    public void run() {
        for (int x = startX; x < endX; x++) {
            for (int y = 0; y < height; y++) {
                double zx = 0;
                double zy = 0;
                double cX = (x - width / 2.0) / (width / 4.0);
                double cY = (y - height / 2.0) / (height / 4.0);
                int iter = 0;
                while (zx * zx + zy * zy < 4 && iter < maxIter) {
                    double tmp = zx * zx - zy * zy + cX;
                    zy = 2.0 * zx * zy + cY;
                    zx = tmp;
                    iter++;
                }
                // Usar la función getColor para determinar el color basado en las iteraciones
                    int color = getColor(iter);
                    image.setRGB(x, y, color);
            }
        }
    }
    
    private int getColor(int iterations) {
        if (iterations == maxIter) {
            return Color.BLACK.getRGB(); // Punto en el conjunto de Mandelbrot
        }
        // Mapear el número de iteraciones a un color
        float hue = (float) iterations / maxIter;
        return Color.HSBtoRGB(hue, 1.0f, 1.0f); // Colores brillantes en el espectro
    }
}

public class MandelbrotConcurrente extends JPanel {
    private final int MAX_ITER = 3000;
    public final int WIDTH = 800;
    public final int HEIGHT = 600;
    private BufferedImage image;

    public MandelbrotConcurrente() {
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    }
    
    public long computeMandelbrot() {
        long startTime = System.currentTimeMillis();
        drawMandelbrot();
        long endTime = System.currentTimeMillis();
        System.out.println("Tiempo de ejecución Concurrente: " + (endTime - startTime) + " ms");
        return endTime - startTime; // Retorna el tiempo de ejecución
    }

    private void drawMandelbrot() {
        int numThreads = 8; // Puedes cambiar el número de hilos aquí
        MandelbrotTask[] tasks = new MandelbrotTask[numThreads];
        int sectionWidth = WIDTH / numThreads;

        // Iniciar los hilos
        for (int i = 0; i < numThreads; i++) {
            
            int startX = i * sectionWidth;
            int endX = (i + 1) * sectionWidth;
            tasks[i] = new MandelbrotTask(startX, endX, WIDTH, HEIGHT, MAX_ITER, image);
            tasks[i].start();
        }


        // Esperar a que todos los hilos terminen
        for (MandelbrotTask task : tasks) {
            try {
                task.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }
    
    private byte[] imageToByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
    
    public byte[] getImageBytes() throws IOException {
        return imageToByteArray();
    }
    
    public void showWindow() {
        JFrame frame = new JFrame("Mandelbrot Set - Concurrencia");
        frame.add(this);
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

}
