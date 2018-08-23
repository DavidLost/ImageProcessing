package de.david.imagepro;

import processing.core.PApplet;
import processing.core.PImage;

import java.io.File;

public class Main extends PApplet {

    public static void main(String[] args) {

        PApplet.main("de.david.imagepro.Main", args);
    }

    PImage image;
    int pointCounter;
    float[] x;
    float[] y;
    float stroke;
    float strokeFactor;
    double pointsPerFrame;
    long totalPoints;
    int average;
    boolean mode;
    boolean selected;
    String fileName;

    public void settings() {

        fullScreen(1);
    }

    public void setup() {

        selectInput("Select a photo", "fileSelected");
        init();
    }

    void init() {

        while (!selected) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
        }
        stroke = 0.5f;
        strokeFactor = 1.01f;
        pointsPerFrame = 25;
        totalPoints = 0;
        println(mode);
        background(41);
        image = loadImage(fileName);
        int max = image.width*2 > image.height ? 0 : 1;
        if (max == 0) image.resize(displayWidth/2, 0);
        else image.resize(0, displayHeight);
        image(image, image.width, 0);
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

    int getAverage() {

        int total = 0;
        image.loadPixels();
        for (int x = 0; x < image.width; x++) {
            for (int y = 0; y < image.height; y++) {
                int index = getIndex(x, y, image.width);
                int col = image.pixels[index];
                float r = red(col);
                float g = green(col);
                float b = blue(col);
                total += (r+g+b)/3;
            }
        }
        return total / (image.width*image.height);
    }

    public void keyPressed() {
        mode = !mode;
        init();
    }

    int getPointNumber() {

        int pointCounter = 0;
        image.loadPixels();
        for (int x = 0; x < image.width; x++) {
            for (int y = 0; y < image.height; y++) {
                int index = getIndex(x, y, image.width);
                if (red(image.pixels[index]) < average && mode) {
                    pointCounter++;
                }
                if (red(image.pixels[index]) > average && !mode) {
                    pointCounter++;
                }
            }
        }
        return pointCounter;
    }

    void setPoints() {

        int counter = 0;
        image.loadPixels();
        for (int x = 0; x < image.width; x++) {
            for (int y = 0; y < image.height; y++) {
                int index = getIndex(x, y, image.width);
                if (red(image.pixels[index]) < average && mode) {
                    this.x[counter] = x;
                    this.y[counter] = y;
                    counter++;
                }
                if (red(image.pixels[index]) > average && !mode) {
                    this.x[counter] = x;
                    this.y[counter] = y;
                    counter++;
                }
            }
        }
    }

    int getIndex(int x, int y, int imageWidth) {
        return x + y * imageWidth;
    }


    float[] getNextPoint() {
        int rnd = (int)random(x.length);
        return new float[] {x[rnd], y[rnd]};
    }

    public void draw() {

        if (totalPoints > 1500000) return;

        for (int i = 0; i < (int)pointsPerFrame; i++) {
            float[] temp = getNextPoint();
            float x = temp[0];
            float y = temp[1];
            float r = map(x+y, 0, image.width+image.height, 0, 255);
            float g = map(x+y, 0, image.width+image.height, 255, 0);
            float b = map(x+image.height-y, 0, image.width+image.height, 255, 0);
            float rgoff = map(x+image.height-y, 0, image.width+image.height, -90, 115);
            r += rgoff;
            g += rgoff;
            strokeWeight(stroke);
            stroke(r, g, b);
            point(x, y);
        }
        totalPoints += (int)pointsPerFrame;
        if (stroke < 2) {
            stroke *= strokeFactor;
        }
        pointsPerFrame *= 1.02;
    }

}
