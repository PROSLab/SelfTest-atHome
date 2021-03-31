package it.overside.distfinder.image;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;


public class ImageUtils {


    public static double calcolateDistance(Point a, Point b){
        return Math.sqrt((a.x-b.x)*(a.x-b.x)+(a.y-b.y)*(a.y-b.y));
    }


    public static Mat drawRectFromPoints(Mat mat, Point[] points, Scalar color){
        Imgproc.line(mat, points[0], points[8],color, 5);
        Imgproc.drawMarker(mat,points[0],color);
        Imgproc.drawMarker(mat,points[8],color);
        return mat;

    }


}
