package Analyzer;
import android.util.Log;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;

public class ObjDrawer extends PApplet{
    PGraphics buffer;
    PShape obj;
    int width;
    int height;
    public ObjDrawer(int width, int height){
        this.width = width;
        this.height = height;
        //Log.d("test", context.getFilesDir().getAbsolutePath();)
        obj = loadShape("Analyzer/Wuerfel_40mm.obj");
        if (obj == null) Log.d("Loadshape", "fale");
        Log.d("Loadeshape", obj.toString());

    }
    @Override
    public void setup(){
        size(width, height);

    }
    public void setSize(int width, int height){
        this.width = width;
        this.height = height;
        setSize(width, height);
    }

    public Mat testDraw(){
        buffer = createGraphics(this.width, this.height);
        buffer.beginDraw();
        buffer.background(255);
        buffer.ellipse(200, 200, 100, 100);
        shape(obj);
        buffer.endDraw();
        Log.d("Processing", "jeet1");
        return toMat(buffer.get());
    }

    Mat toMat(PImage image) {
        //source: https://gist.github.com/Spaxe/3543f0005e9f8f3c4dc5
        int w = image.width;
        int h = image.height;

        Mat mat = new Mat(h, w, CvType.CV_8UC4);
        byte[] data8 = new byte[w*h*4];
        int[] data32 = new int[w*h];
        arrayCopy(image.pixels, data32);
        ByteBuffer bBuf = ByteBuffer.allocate(w*h*4);
        IntBuffer iBuf = bBuf.asIntBuffer();
        iBuf.put(data32);
        bBuf.get(data8);
        mat.put(0, 0, data8);
        Log.d("Processing", "jeet5");
        return mat;
    }
}
