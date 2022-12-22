package Analyzer;

import android.util.Log;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.aruco.DetectorParameters;
import org.opencv.aruco.Dictionary;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.aruco.Aruco;

import java.util.ArrayList;
import java.util.List;

public class ImageAnalyzer {


    private Mat lines;
    private List<MatOfPoint> contours;
    private List<Mat> arucoCorners;

    public ImageAnalyzer() {
        lines = new Mat();
        contours = new ArrayList<>();
        arucoCorners = new ArrayList<>();
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

    public void analyze(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat inputMat = inputFrame.rgba();
        this.lines = detectLines(inputMat);
        contours = detectObj(inputMat);
        //arucoCorners = detectAruco(inputMat);
       // Log.d("Aruco", String.valueOf(arucoCorners));
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
    private List<MatOfPoint> detectObj(Mat matRaw) {
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


    private List<Mat> detectAruco(Mat matRaw) {
        List<Mat> corners = new ArrayList<>();
        Mat ids = new Mat();
        DetectorParameters parameters = DetectorParameters.create();
        Dictionary arucoDict = Aruco.getPredefinedDictionary(Aruco.DICT_5X5_50);
        Aruco.detectMarkers(matRaw, arucoDict, corners, ids, parameters);
        return corners;
    }

    private double getPixelToCM(){
        int perimeterCM = 20;
        double perimeterPixel = Imgproc.arcLength((MatOfPoint2f) arucoCorners.get(0),true);
        return (perimeterPixel/perimeterCM);
    }
}
