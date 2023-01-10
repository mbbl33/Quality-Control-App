package com.quickbirdstudios.opencvexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import Analyzer.ObjDrawer;
import processing.android.PFragment;
import processing.android.CompatUtils;
import processing.core.PApplet;


public class DummyForProcessing extends AppCompatActivity {



    private ObjDrawer objDrawer;


    public ObjDrawer getObjDrawer() {
        return objDrawer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frame = new FrameLayout(this);
        frame.setId(CompatUtils.getUniqueViewId());
        setContentView(frame, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        objDrawer = new ObjDrawer();
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