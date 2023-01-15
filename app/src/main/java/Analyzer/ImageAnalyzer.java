package Analyzer;

import android.util.Log;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.aruco.DetectorParameters;
import org.opencv.aruco.Dictionary;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.aruco.Aruco;

import java.util.ArrayList;
import java.util.List;

public class ImageAnalyzer {


    private Mat lines;
    private List<MatOfPoint> contours;
    private List<Mat> arucoCorners;

    private ArrayList<RotatedRect> rects = new ArrayList<>();

    public ImageAnalyzer() {
        lines = new Mat();
        contours = new ArrayList<>();
        arucoCorners = new ArrayList<>();
        rects = new ArrayList<>();
    }

    public Mat getLines() {
        return lines.clone();
    }

    public List<MatOfPoint> getContours() {
        return contours;
    }

    public List<Mat> getArucoCorners() {
        return arucoCorners;
    }

    public ArrayList<RotatedRect> getRects() {
        return rects;
    }

    public void analyze(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        cleanDate();
        Mat inputMat = inputFrame.rgba();
        this.lines = detectLines(inputMat);
        contours = detectObj(inputMat);
        arucoCorners = detectAruco(inputMat);
        if (arucoDetected()) {
            rects = contoursToRect(contours);
            Log.d("Aruco pixel cm ", Double.toString(getPixelToCM()));
        }
    }

    private boolean arucoDetected() {
        return 0 < arucoCorners.size();
    }

    private void cleanDate() {
        lines = new Mat();
        contours = new ArrayList<>();
        arucoCorners = new ArrayList<>();
        rects = new ArrayList<>();
    }

    private Mat detectLines(Mat matRaw) {
        Mat imgGray = new Mat();
        Mat imgEdges = new Mat();
        Mat lines = new Mat();
        Imgproc.cvtColor(matRaw, imgGray, Imgproc.COLOR_RGB2GRAY);
        Imgproc.blur(imgGray, imgEdges, new Size(3, 3));
        Imgproc.Canny(imgEdges, imgEdges, 10, 40);
        Imgproc.HoughLinesP(imgEdges, lines, 1, Math.PI / 180, 15, 15, 10); //TODO: find better values
        return lines;
    }


    //source: https://www.youtube.com/watch?v=lbgl2u6KrDU
    public List<MatOfPoint> detectObj(Mat matRaw) {
        Mat imgGray = new Mat();
        Mat mask = new Mat();
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        List<MatOfPoint> objects_contours = new ArrayList<>();
        Imgproc.cvtColor(matRaw, imgGray, Imgproc.COLOR_RGB2GRAY);
        Imgproc.adaptiveThreshold(imgGray, mask, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 19, 5); //Create a Mask with adaptive threshold
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        for (MatOfPoint cnt : contours) {
            double area = Imgproc.contourArea(cnt);
            if (area > 2000) {
                objects_contours.add(cnt);
            }
        }
        return objects_contours;
    }

    private ArrayList<Mat> detectAruco(Mat matRaw) {
        Mat imgGray = new Mat();
        MatOfInt ids = new MatOfInt();
        ArrayList<Mat> corners = new ArrayList<>();
        Imgproc.cvtColor(matRaw, imgGray, Imgproc.COLOR_RGB2GRAY);
        DetectorParameters parameters = DetectorParameters.create();
        Dictionary arucoDict = Aruco.getPredefinedDictionary(Aruco.DICT_5X5_50);
        Aruco.detectMarkers(imgGray, arucoDict, corners, ids, parameters);
        return corners;
    }

    private double getPixelToCM() {
        if (!arucoDetected()) return -1;
        int perimeterCM = 20;
        Rect rect = Imgproc.boundingRect(arucoCorners.get(0));
        double perimeterAruco = 2 * rect.height + 2 * rect.width;
        Log.d("Aruco perimeter", Double.toString(perimeterAruco));
        return (perimeterAruco / perimeterCM);
    }

    private double getPixelToCM_2() {
        if (!arucoDetected()) return -1;
        int areaCM_2 = 25;
        double arucoArea = Imgproc.contourArea(arucoCorners.get(0));
        return arucoArea / areaCM_2;
    }

    private RotatedRect getRect(MatOfPoint matPts) {
        MatOfPoint2f pts = new MatOfPoint2f(matPts.toArray());
        return Imgproc.minAreaRect(pts);
    }

    private ArrayList<RotatedRect> contoursToRect(List<MatOfPoint> contours) {
        ArrayList<RotatedRect> rects = new ArrayList<>();
        for (MatOfPoint cnt : contours) {
            rects.add(getRect(cnt));
        }
        return rects;
    }

    public double getHeightInCM(RotatedRect rect) {
        return rect.size.height / getPixelToCM();
    }

    public double getWidthInCM(RotatedRect rect) {
        return rect.size.width / getPixelToCM();
    }
    public double getAreaInCM_2(RotatedRect rect){
        return rect.size.area() / getPixelToCM_2();
    }
}
