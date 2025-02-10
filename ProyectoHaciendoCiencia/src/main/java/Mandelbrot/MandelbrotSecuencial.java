/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Mandelbrot;

import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MandelbrotSecuencial extends JPanel {
    private final int MAX_ITER = 3000;
    public final int WIDTH = 800;
    public final int HEIGHT = 600;
    private BufferedImage image;

    public MandelbrotSecuencial() {
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    }
    
    public long computeMandelbrot() {
        long startTime = System.currentTimeMillis();
        drawMandelbrot();
        long endTime = System.currentTimeMillis();
        System.out.println("Tiempo de ejecución Secuencial: " + (endTime - startTime) + " ms");
        return endTime - startTime; // Retorna el tiempo de ejecución
    }

    private void drawMandelbrot() {
        
        for (int x = 0; x < WIDTH; x++) {
                for (int y = 0; y < HEIGHT; y++) {
                    double zx = 0;
                    double zy = 0;
                    double cX = (x - WIDTH / 2.0) / (WIDTH / 4.0);
                    double cY = (y - HEIGHT / 2.0) / (HEIGHT / 4.0);
                    int iter = 0;
                    while (zx * zx + zy * zy < 4 && iter < MAX_ITER) {
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
        if (iterations == MAX_ITER) {
            return Color.BLACK.getRGB(); // Punto en el conjunto de Mandelbrot
        }
        // Mapear el número de iteraciones a un color
        float hue = (float) iterations / MAX_ITER;
        return Color.HSBtoRGB(hue, 1.0f, 1.0f); // Colores brillantes en el espectro
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

}
