
package prueba;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import static java.util.concurrent.ForkJoinTask.invokeAll;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

class MandelbrotTask implements Callable<Void> {
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
    public Void call() {
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
                //int color = iter | (iter << 8);
                //if (iter == maxIter) color = 0; // black
                //image.setRGB(x, y, color);
                // Usar la función getColor para determinar el color basado en las iteraciones
                    int color = getColor(iter);
                    image.setRGB(x, y, color);
            }
        }
        return null;
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

public class mandelprueba extends JPanel {
    private final int MAX_ITER = 100000;
    public final int WIDTH = 800;
    public final int HEIGHT = 600;
    private BufferedImage image;

    public mandelprueba() {
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    }
    
    public long computeMandelbrot() {
        long startTime = System.currentTimeMillis();
        drawMandelbrot();
        long endTime = System.currentTimeMillis();
        System.out.println("Tiempo de ejecución Paralelo: " + (endTime - startTime) + " ms");
        return endTime - startTime; // Retorna el tiempo de ejecución
    }

     private void drawMandelbrot() {
        int numThreads = 8;
        int sectionWidth = WIDTH / numThreads;
        
        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            int startX = i * sectionWidth;
            int endX = (i + 1) * sectionWidth;
            tasks.add(new MandelbrotTask(startX, endX, WIDTH, HEIGHT, MAX_ITER, image));
        }
        
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        
        try {
            executor.invokeAll(tasks); // Espera a que todas las tareas terminen
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
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
        JFrame frame = new JFrame("Mandelbrot Set - Paralelo");
        frame.add(this);
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        mandelprueba mandelbrot = new mandelprueba();
        mandelbrot.computeMandelbrot(); // Calcular Mandelbrot al iniciar
        mandelbrot.showWindow(); // Mostrar la ventana
    }
}
 
