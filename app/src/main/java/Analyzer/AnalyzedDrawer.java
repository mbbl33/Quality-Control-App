package Analyzer;

import android.annotation.SuppressLint;
import android.util.Log;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
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
        //img = drawLinesOnImg(img, imageAnalyzer.getLines());
        img = drawContours(img, imageAnalyzer.getContours());
        img = drawSizes(img);
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

    public Mat drawContours(Mat matRaw, List<MatOfPoint> contours) {
        Mat out = matRaw.clone();
        Imgproc.polylines(out, contours, true, new Scalar(0, 0, 255), 2);
        return out;
    }


    public Mat drawSizes(Mat matRaw) {
        Scalar textColor = new Scalar(0, 255, 0);
        int textOffset = 45;
        int textFontSize = 1;
        Mat out = matRaw.clone();
        for (RotatedRect rect : imageAnalyzer.getRects()) {
            Imgproc.circle(out, rect.center, 1, textColor, 10, Imgproc.LINE_AA);
            Imgproc.putText(out, String.format("Width %f cm", imageAnalyzer.getWidthInCM(rect)), new Point(rect.center.x, rect.center.y + textOffset * 2), Core.FONT_HERSHEY_TRIPLEX, textFontSize, textColor);
            Imgproc.putText(out, String.format("Height %f cm", imageAnalyzer.getHeightInCM(rect)), new Point(rect.center.x, rect.center.y + textOffset), Core.FONT_HERSHEY_TRIPLEX, textFontSize, textColor);
            Imgproc.putText(out, String.format("Area %f cm^2", imageAnalyzer.getAreaInCM_2(rect)), rect.center, Core.FONT_HERSHEY_TRIPLEX, textFontSize, textColor);
        }
        return out;
    }
}
