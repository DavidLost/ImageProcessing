package de.david.imagepro;

import processing.core.PApplet;
import processing.core.PImage;

import javax.swing.*;
import java.io.File;

public class Window extends PApplet {

    private PImage image;
    private int pointCounter;
    private float[] x;
    private float[] y;
    private float stroke;
    private float strokeFactor;
    private double pointsPerFrame;
    private long totalPoints;
    private int average;
    private boolean mode;
    private boolean selected;
    private boolean showOrg = true;
    private boolean finished;
    private String fileName;

    public void settings() {

        fullScreen(1);
    }

    public void setup() {

        selectInput("Select a photo", "fileSelected");
        init();
    }

    private void init() {

        while (!selected) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
        }
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        showOrg = JOptionPane.showConfirmDialog(null, "Show orginal pic?", "Select", JOptionPane.YES_NO_OPTION) != 1;
        stroke = 0.5f;
        strokeFactor = 1.01f;
        pointsPerFrame = 25;
        totalPoints = 0;
        finished = false;
        background(41);
        image = loadImage(fileName);
        System.out.println(image.width+" - "+image.height);
        int max;
        if (showOrg) max = image.width/8 > image.height/9 ? 0 : 1;
        else max = image.width/16 > image.height/9 ? 0 : 1;
        if (max == 0) {
            if (showOrg) image.resize(displayWidth/2, 0);
            else image.resize(displayWidth, 0);
        }
        else {
            image.resize(0, displayHeight);
        }
        System.out.println(image.width+" - "+image.height);
        if (showOrg) image(image, image.width, 0);
        average = getAverage();
        pointCounter = getPointNumber();
        x = new float[pointCounter];
        y = new float[pointCounter];
        setPoints();
    }


    public void fileSelected(File selection) {

        if (selection == null) System.exit(0);
        fileName = selection.getAbsolutePath();
        if (!fileName.endsWith(".jpg") && !fileName.endsWith(".png") && !fileName.endsWith(".jpeg")) System.exit(0);
        selected = true;
    }

    private int getAverage() {

        int total = 0;
        image.loadPixels();
        for (int x = 0; x < image.width; x++) {
            for (int y = 0; y < image.height; y++) {
                int index = getIndex(x, y, image.width);
                total += getRGBAverage(image.pixels[index]);
            }
        }
        return total / (image.width*image.height);
    }

    public void keyPressed() {
        mode = !mode;
        init();
    }

    private int getPointNumber() {

        int pointCounter = 0;
        image.loadPixels();
        for (int x = 0; x < image.width; x++) {
            for (int y = 0; y < image.height; y++) {
                int index = getIndex(x, y, image.width);
                if (getRGBAverage(image.pixels[index]) < average && mode) {
                    pointCounter++;
                }
                else if (getRGBAverage(image.pixels[index]) > average && !mode) {
                    pointCounter++;
                }
            }
        }
        return pointCounter;
    }

    private void setPoints() {

        int counter = 0;
        image.loadPixels();
        for (int x = 0; x < image.width; x++) {
            for (int y = 0; y < image.height; y++) {
                int index = getIndex(x, y, image.width);
                if (getRGBAverage(image.pixels[index]) < average && mode) {
                    this.x[counter] = x;
                    this.y[counter] = y;
                    counter++;
                }
                if (getRGBAverage(image.pixels[index]) > average && !mode) {
                    this.x[counter] = x;
                    this.y[counter] = y;
                    counter++;
                }
            }
        }
    }

    private int getIndex(int x, int y, int imageWidth) {
        return x + y * imageWidth;
    }

    private float[] getNextPoint() {
        int rnd = (int)random(x.length);
        return new float[] {x[rnd], y[rnd]};
    }

    public void draw() {

        if (finished) return;

        for (int i = 0; i < (int)pointsPerFrame; i++) {
            float[] temp = getNextPoint();
            drawPoint(temp[0], temp[1]);
        }
        totalPoints += (int)pointsPerFrame;
        if (stroke < 1.6) {
            stroke *= strokeFactor;
        }
        pointsPerFrame *= 1.018;
        if (pointsPerFrame > 12000) {
            fastFill();
            finished = true;
        }
    }

    private void drawPoint(float x, float y) {

        float r = map(x+y, 0, image.width+image.height, 0, 255);
        float g = map(x+y, 0, image.width+image.height, 255, 0);
        float b = map(x+image.height-y, 0, image.width+image.height, 255, 0);
        float rgoff = map(x+image.height-y, 0, image.width+image.height, -90, 110);
        r += rgoff;
        g += rgoff;
        strokeWeight(stroke);
        stroke(r, g, b);
        point(x, y);
    }

    private void fastFill() {
        for (int i = 0; i < x.length; i++) {
            drawPoint(x[i], y[i]);
        }
    }

    private float getRGBAverage(int col) {
        return (red(col)+green(col)+blue(col))/3f;
    }

}
