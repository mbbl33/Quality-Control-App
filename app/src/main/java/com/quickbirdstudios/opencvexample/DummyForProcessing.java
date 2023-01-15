package com.quickbirdstudios.opencvexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.opencv.core.Mat;

import Analyzer.ObjDrawer;
import processing.android.PFragment;
import processing.android.CompatUtils;
import processing.core.PApplet;


public class DummyForProcessing extends AppCompatActivity {


    private ObjDrawer objDrawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frame = new FrameLayout(this);
        frame.setId(CompatUtils.getUniqueViewId());
        setContentView(frame, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        objDrawer = new ObjDrawer();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            objDrawer.setWidth(extras.getInt("width"));
            objDrawer.setHeight(extras.getInt("height"));
            ObjDrawer.Perspective perspective;
            switch (extras.getString("perspective")) {
                case "top":
                    perspective = ObjDrawer.Perspective.TOP;
                    break;
                case "left":
                    perspective = ObjDrawer.Perspective.LEFT;
                    break;
                case "right":
                    perspective = ObjDrawer.Perspective.RIGHT;
                    break;
                case "bottom":
                    perspective = ObjDrawer.Perspective.BOTTOM;
                    break;
                case "back":
                    perspective = ObjDrawer.Perspective.BACK;
                    break;
                default:
                    perspective = ObjDrawer.Perspective.FRONT;
                    break;
            }
            objDrawer.setCurrentView(perspective);
        }
        PFragment fragment = new PFragment(objDrawer);
        fragment.setView(frame, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (objDrawer != null) {
            objDrawer.onRequestPermissionsResult(
                    requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (objDrawer != null) {
            objDrawer.onNewIntent(intent);
        }

    }
}