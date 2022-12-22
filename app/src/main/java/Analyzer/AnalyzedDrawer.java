package Analyzer;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;

public class AnalyzedDrawer {
    private ImageAnalyzer imageAnalyzer;

    public AnalyzedDrawer(ImageAnalyzer imageAnalyzer) {
        this.imageAnalyzer = imageAnalyzer;
    }

    public Mat draw(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat img = inputFrame.rgba();
        img = drawLinesOnImg(img, imageAnalyzer.getLines());
        img = drawContours(img, imageAnalyzer.getContours());
        return img;
    }

    public Mat drawLinesOnImg(Mat matRaw, Mat lines) {
        Mat out = matRaw.clone();
        for (int i = 0; i < lines.rows(); i++) {
            double[] l = lines.get(i, 0);
            Imgproc.line(out, new Point(l[0], l[1]), new Point(l[2], l[3]), new Scalar(255, 0, 0), 2, Imgproc.LINE_AA, 0);
        }
        return out;
    }

    public Mat drawContours(Mat inputFrame, List<MatOfPoint> contours) {
        Mat out = inputFrame.clone();
        Imgproc.polylines(out, contours, true, new Scalar(0, 0, 255), 2);
        return out;
    }
}
