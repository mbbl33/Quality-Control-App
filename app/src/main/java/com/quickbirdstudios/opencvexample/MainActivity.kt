package com.quickbirdstudios.opencvexample

import Analyzer.AnalyzedDrawer
import Analyzer.ImageAnalyzer
import Analyzer.MatBox
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
import org.opencv.core.Scalar

class MainActivity : AppCompatActivity() {
    private val LOGTAG = "OpenCV_Log"
    private var mOpenCvCameraView: CameraBridgeViewBase? = null

    /*var drawView  : DrawView? = null //Initialising draw object*/
    var imageAnalyzer: ImageAnalyzer? = null
    var analyzedDrawer: AnalyzedDrawer? = null
    var width: Int = -1
    var height: Int = -1

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
            width = inputFrame.rgba().width()
            height = inputFrame.rgba().height()
            imageAnalyzer?.analyze(inputFrame)
            var out: Mat? = analyzedDrawer?.draw(inputFrame)
            if (MatBox.mat != null) {

               out = analyzedDrawer?.drawContours( out,imageAnalyzer?.detectObj(MatBox.mat),
                   Scalar(255.0, 0.0, 0.0))
            }
            return out ?: return inputFrame.rgba()
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
        intent.putExtra("perspective", view?.tag.toString())
        intent.putExtra("width", width)
        intent.putExtra("height", height)
        startActivity(intent)
    }
}