package Analyzer;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.renderscript.ScriptGroup;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PSurface;
import processing.opengl.PGraphics3D;

public class ObjDrawer extends PApplet {

    PGraphics3D buffer;
    PShape obj;
    PImage img;
    AppCompatActivity activity;
    int width;
    int height;

    public ObjDrawer(int width, int height, AppCompatActivity activity) throws IOException {
        super();
        super.g = new PGraphics3D();
        this.width = width;
        this.height = height;
        this.activity = activity;
        //img = loadImage("107433517.png");
        //Log.d("assa", obj.toString());
    }

    String loadTOCache(String fileName){
       // https://stackoverflow.com/questions/8474821/how-to-get-the-android-path-string-to-a-file-on-assets-folder
        File f = new File(activity.getCacheDir()+"/" + fileName);
        if (!f.exists()) try {

            InputStream is = activity.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(buffer);
            fos.close();
        } catch (Exception e) { throw new RuntimeException(e); }

        return f.getPath();
    }


    @Override
    public void setup() {
        size(width, height,P3D);
    }

    @Override
    public void draw() {
        buffer.background(255);
    }

    public Mat testDraw() {
        buffer = (PGraphics3D) createGraphics(this.width, this.height,P3D);
        obj = buffer.loadShape("Wuerfel_40mm.obj");
        buffer.beginDraw();
        buffer.background(255);
        buffer.ellipse(200, 200, 100, 100);

        Log.d("assa4", obj.toString());
        if (obj != null) {
            Log.d("assa5", "ja ist nicht null");
        }else {
            Log.d("assa5","ja ist null");
        }
        Log.d("Processing", "jeet1");
        shape(obj);
        buffer.endDraw();
        return toMat(buffer.get());
    }

    Mat toMat(PImage image) {
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

    @Override
     public Activity getActivity(){
        return this.activity;
    }

    @Override
    public InputStream createInputRaw(String filename) {
        // Additional considerations for Android version:
        // http://developer.android.com/guide/topics/resources/resources-i18n.html
        InputStream stream = null;

        if (filename == null) return null;

        if (filename.length() == 0) {
            // an error will be called by the parent function
            //System.err.println("The filename passed to openStream() was empty.");
            return null;
        }

        // safe to check for this as a url first. this will prevent online
        // access logs from being spammed with GET /sketchfolder/http://blahblah
        if (filename.indexOf(":") != -1) {  // at least smells like URL
            try {
                // Workaround for Android bug 6066
                // http://code.google.com/p/android/issues/detail?id=6066
                // http://code.google.com/p/processing/issues/detail?id=629
//      URL url = new URL(filename);
//      stream = url.openStream();
//      return stream;
                URL url = new URL(filename);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setDoInput(true);
                con.connect();
                return con.getInputStream();
                //The following code is deprecaded by Android
//        HttpGet httpRequest = null;
//        httpRequest = new HttpGet(URI.create(filename));
//        HttpClient httpclient = new DefaultHttpClient();
//        HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
//        HttpEntity entity = response.getEntity();
//        return entity.getContent();
                // can't use BufferedHttpEntity because it may try to allocate a byte
                // buffer of the size of the download, bad when DL is 25 MB... [0200]
//        BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
//        return bufHttpEntity.getContent();

            } catch (MalformedURLException mfue) {
                // not a url, that's fine

            } catch (FileNotFoundException fnfe) {
                // Java 1.5 likes to throw this when URL not available. (fix for 0119)
                // http://dev.processing.org/bugs/show_bug.cgi?id=403

            } catch (IOException e) {
                // changed for 0117, shouldn't be throwing exception
                printStackTrace(e);
                //System.err.println("Error downloading from URL " + filename);
                return null;
                //throw new RuntimeException("Error downloading from URL " + filename);
            }
        }

    /*
    // Moved this earlier than the getResourceAsStream() checks, because
    // calling getResourceAsStream() on a directory lists its contents.
    // http://dev.processing.org/bugs/show_bug.cgi?id=716
    try {
      // First see if it's in a data folder. This may fail by throwing
      // a SecurityException. If so, this whole block will be skipped.
      File file = new File(dataPath(filename));
      if (!file.exists()) {
        // next see if it's just in the sketch folder
        file = new File(sketchPath, filename);
      }
      if (file.isDirectory()) {
        return null;
      }
      if (file.exists()) {
        try {
          // handle case sensitivity check
          String filePath = file.getCanonicalPath();
          String filenameActual = new File(filePath).getName();
          // make sure there isn't a subfolder prepended to the name
          String filenameShort = new File(filename).getName();
          // if the actual filename is the same, but capitalized
          // differently, warn the user.
          //if (filenameActual.equalsIgnoreCase(filenameShort) &&
          //!filenameActual.equals(filenameShort)) {
          if (!filenameActual.equals(filenameShort)) {
            throw new RuntimeException("This file is named " +
                                       filenameActual + " not " +
                                       filename + ". Rename the file " +
            "or change your code.");
          }
        } catch (IOException e) { }
      }
      // if this file is ok, may as well just load it
      stream = new FileInputStream(file);
      if (stream != null) return stream;
      // have to break these out because a general Exception might
      // catch the RuntimeException being thrown above
    } catch (IOException ioe) {
    } catch (SecurityException se) { }
     */

        // Using getClassLoader() prevents Java from converting dots
        // to slashes or requiring a slash at the beginning.
        // (a slash as a prefix means that it'll load from the root of
        // the jar, rather than trying to dig into the package location)

    /*
    // this works, but requires files to be stored in the src folder
    ClassLoader cl = getClass().getClassLoader();
    stream = cl.getResourceAsStream(filename);
    if (stream != null) {
      String cn = stream.getClass().getName();
      // this is an irritation of sun's java plug-in, which will return
      // a non-null stream for an object that doesn't exist. like all good
      // things, this is probably introduced in java 1.5. awesome!
      // http://dev.processing.org/bugs/show_bug.cgi?id=359
      if (!cn.equals("sun.plugin.cache.EmptyInputStream")) {
        return stream;
      }
    }
     */

        // Try the assets folder
        AssetManager assets = this.activity.getAssets();
        try {
            stream = assets.open(filename);
            if (stream != null) {
                return stream;
            }
        } catch (IOException e) {
            // ignore this and move on
            //e.printStackTrace();
        }

        // Maybe this is an absolute path, didja ever think of that?
        File absFile = new File(filename);
        if (absFile.exists()) {
            try {
                stream = new FileInputStream(absFile);
                if (stream != null) {
                    return stream;
                }
            } catch (FileNotFoundException fnfe) {
                //fnfe.printStackTrace();
            }
        }

        // Maybe this is a file that was written by the sketch on another occasion.
        File sketchFile = new File(sketchPath(filename));
        if (sketchFile.exists()) {
            try {
                stream = new FileInputStream(sketchFile);
                if (stream != null) {
                    return stream;
                }
            } catch (FileNotFoundException fnfe) {
                //fnfe.printStackTrace();
            }
        }

        // Attempt to load the file more directly. Doesn't like paths.
//    try {
//      // MODE_PRIVATE is default, should we use something else?
//      stream = surface.openFileInput(filename);
//      if (stream != null) {
//        return stream;
//      }
//    } catch (FileNotFoundException e) {
//      // ignore this and move on
//      //e.printStackTrace();
//    }

        return surface.openFileInput(filename);
    }
}
