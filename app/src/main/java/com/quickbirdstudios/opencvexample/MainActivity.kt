package com.quickbirdstudios.opencvexample

import Analyzer.AnalyzedDrawer
import Analyzer.ImageAnalyzer
import Analyzer.ObjDrawer
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat
import com.quickbirdstudios.opencvexample.DummyForProcessing

class MainActivity : AppCompatActivity() {
    private val LOGTAG = "OpenCV_Log"
    private var mOpenCvCameraView: CameraBridgeViewBase? = null

    /*var drawView  : DrawView? = null //Initialising draw object*/
    var imageAnalyzer: ImageAnalyzer? = null
    var analyzedDrawer: AnalyzedDrawer? = null
    var objDrawer: ObjDrawer? = ObjDrawer()

    private val mLoaderCallback: BaseLoaderCallback = object : BaseLoaderCallback(this) {
        override fun onManagerConnected(status: Int) {
            when (status) {
                SUCCESS -> {
                    Log.d(LOGTAG, "OpenCV Loaded")
                    mOpenCvCameraView!!.enableView()
                    imageAnalyzer = ImageAnalyzer()
                    analyzedDrawer = AnalyzedDrawer(imageAnalyzer)
                }
                else -> {
                    super.onManagerConnected(status)
                }
            }
            super.onManagerConnected(status)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.quickbirdstudios.opencvexample.R.layout.activity_main)
        mOpenCvCameraView = findViewById(R.id.opencv_surface_view) as CameraBridgeViewBase
        mOpenCvCameraView!!.visibility = SurfaceView.VISIBLE
        mOpenCvCameraView!!.setCvCameraViewListener(cvCameraViewListener)


    }

    protected fun getCameraViewList(): List<CameraBridgeViewBase?>? {
        return listOf(mOpenCvCameraView)
    }

    private val cvCameraViewListener: CvCameraViewListener2 = object : CvCameraViewListener2 {
        override fun onCameraViewStarted(width: Int, height: Int) {}
        override fun onCameraViewStopped() {}

        /**
         *
         * @param inputFrame the current camera image
         * @return the image to be displayed on the screen
         */
        override fun onCameraFrame(inputFrame: CvCameraViewFrame): Mat {
            /*if (!objDrawer!!.isSizeSet() {
                objDrawer!!.setHeight(inputFrame.rgba().height())
                objDrawer!!.setWidth(inputFrame.rgba().width())
                objDrawer!!.setup()
            }
            return objDrawer?.testDraw() ?: return inputFrame.rgba();*/
            imageAnalyzer?.analyze(inputFrame)
            return analyzedDrawer?.draw(inputFrame) ?: return inputFrame.rgba()
        }
    }

    override fun onPause() {
        super.onPause()
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView!!.disableView()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!OpenCVLoader.initDebug()) {
            Log.d(LOGTAG, "OpenCv not found, Inti")
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback)
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView!!.disableView()
        }
    }

    fun openProcessing(view: View?) {
        //just for testing
        val intent = Intent(this, DummyForProcessing::class.java)
        startActivity(intent)
    }
}