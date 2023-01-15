package Analyzer;

import android.content.Intent;
import android.util.Log;

import com.quickbirdstudios.opencvexample.MainActivity;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import processing.android.PFragment;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.opengl.PGraphics3D;


public class ObjDrawer extends PApplet {



    private PGraphics buffer;
    private PShape obj;
    private boolean isSizeSet = false;
    private Perspective currentView = Perspective.TOP;

    public boolean isSizeSet() {
        return width != -1 && height != -1;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Perspective getCurrentView() {
        return currentView;
    }

    public void setCurrentView(Perspective currentView) {
        this.currentView = currentView;
    }

    public PGraphics getBuffer() {
        return buffer;
    }

    public void settings() {
        size(width, height, P3D);
    }

    public void setup() {
        buffer = createGraphics(width, height, P3D);
        obj = buffer.loadShape("Bird.obj");
    }

    @Override
    public void draw() {
        Mat mat = drawObjOffscreen();
        MatBox.mat = mat;
        image(buffer, 0, 0);
        Intent intent = new Intent(this.getActivity(),MainActivity.class);
        startActivity(intent);
    }


    public Mat drawObjOffscreen() {
        buffer.beginDraw();
        buffer.background(255);
        buffer.translate(width / 2, height / 2, 500);
        setView(currentView);
       // rotateForDemo(); //just for Demo
        buffer.shape(obj);
        buffer.endDraw();
        return toMat(buffer.get());
    }

    public Mat toMat(PImage image) {
        //converts a processing img to opencv matrix
        //source: https://gist.github.com/Spaxe/3543f0005e9f8f3c4dc5
        int w = image.width;
        int h = image.height;
        Mat mat = new Mat(h, w, CvType.CV_8UC4);
        byte[] data8 = new byte[w * h * 4];
        int[] data32 = new int[w * h];
        arrayCopy(image.pixels, data32);
        ByteBuffer bBuf = ByteBuffer.allocate(w * h * 4);
        IntBuffer iBuf = bBuf.asIntBuffer();
        iBuf.put(data32);
        bBuf.get(data8);
        mat.put(0, 0, data8);
        return mat;
    }

    public enum Perspective {
        TOP,
        BOTTOM,
        FRONT,
        LEFT,
        RIGHT,
        BACK;
    }

    private void setView(Perspective p) {
        switch (p) {
            case TOP:
                buffer.rotateX(-HALF_PI);
                break;
            case BOTTOM:
                buffer.rotateX(HALF_PI);
                break;
            case LEFT:
                buffer.rotateY(HALF_PI);
                break;
            case RIGHT:
                buffer.rotateY(-HALF_PI);
                break;
            case BACK:
                buffer.rotateX(PI);
                break;
            case FRONT:
                break;
        }
    }

    //this is just for demo that we can turn the .obj file
    float x = 0;
    public void rotateForDemo() {
        x += 0.1;
        buffer.rotateX(x);
        buffer.rotateY(x);
    }
}