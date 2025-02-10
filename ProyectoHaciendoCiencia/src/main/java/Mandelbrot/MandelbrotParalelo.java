/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Mandelbrot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;
import static java.util.concurrent.ForkJoinTask.invokeAll;
import java.util.concurrent.RecursiveAction;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author Alexis
 */
public class MandelbrotParalelo extends JPanel {
    private final int MAX_ITER = 3000; // Se declaran los parametros de la imagen a generar
    public final int WIDTH = 800;
    public final int HEIGHT = 600;
    private BufferedImage image;

    public MandelbrotParalelo() {
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    }
    
    public long computeMandelbrot() { //Se cuenta el tiempo que le lleva a la clase generar la imagen
        long startTime = System.currentTimeMillis();
        drawMandelbrot();
        long endTime = System.currentTimeMillis();
        System.out.println("Tiempo de ejecución Paralelo: " + (endTime - startTime) + " ms");
        return endTime - startTime; // Retorna el tiempo de ejecución
    }

    private void drawMandelbrot() { //Se ejecuta el task, que es la clase que está definida abajo
        //Esta al ser paralela tiene que declarase de esa manera separada
        ForkJoinPool pool = new ForkJoinPool();
        MandelbrotParaleloTask task = new MandelbrotParaleloTask(0, WIDTH, WIDTH, HEIGHT, MAX_ITER, image);
        pool.invoke(task);
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

}



class MandelbrotParaleloTask extends RecursiveAction {
    private final int startX, endX, width, height, maxIter;
    private final BufferedImage image;
    private static final int THRESHOLD = 100; // Umbral para dividir el trabajo, es para decidir
    //cuando es necesaria hacer la division

    public MandelbrotParaleloTask(int startX, int endX, int width, int height, int maxIter, BufferedImage image) {
        this.startX = startX;
        this.endX = endX;
        this.width = width;
        this.height = height;
        this.maxIter = maxIter;
        this.image = image;
    }

    @Override
    protected void compute() {
        if (endX - startX <= THRESHOLD) { //En caso de que la tarea sea lo suficientemente "ligera", deja de
            //tratar de dividirse y ahora si se ejecuta el trazado de la imagen
            for (int x = startX; x < endX; x++) {
                for (int y = 0; y < height; y++) { //Proceso del dibujo de mandelbrot
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
        } else { // En caso de que la tarea sea muy "pesada", se divide en dos, debido al umbral establecido
            //siempre se realiza esta division, porque la tarea es ejecutar en paralelo y que se vea la mejora
            //del tiempo
            int mid = (startX + endX) / 2;
            invokeAll(new MandelbrotParaleloTask(startX, mid, width, height, maxIter, image),
                       new MandelbrotParaleloTask(mid, endX, width, height, maxIter, image));
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
