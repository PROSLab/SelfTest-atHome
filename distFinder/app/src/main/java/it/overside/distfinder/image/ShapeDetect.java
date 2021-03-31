package it.overside.distfinder.image;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ShapeDetect {

    public static final int MAX_WIDTH = 1300;
    public static final int MAX_HEIGHT = 1000;



    List<MatOfPoint> contours = new ArrayList<>();

    /**
     * Immagine originale
     */
    protected Mat srcImage;


    private ShapeResult result = new ShapeResult();



    /**
     * Inizializza oggetto
     *
     * @param width
     * @param height
     */
    public void init(int width, int height) {
         }


    /**
     * Ricerca A4 nell'immagine
     *
     * @param image
     * @return
     */
    public ShapeResult findChess(Mat image) {


        srcImage = image;
        Size board_sz = new Size(9, 6);
        MatOfPoint2f corners = new MatOfPoint2f();
        boolean found = Calib3d.findChessboardCorners(image, board_sz, corners, Calib3d.CALIB_CB_ADAPTIVE_THRESH | Calib3d.CALIB_CB_FILTER_QUADS);
        if(found){
            Imgproc.cornerSubPix(image, corners, new Size(5, 5), new Size(-1, -1),new TermCriteria(TermCriteria.EPS, 30, 0.0001));

            result.points = corners.toArray();
            result.distance= ImageUtils.calcolateDistance(result.points[0],result.points[8]);


            return result ;
        }
        return null;
    }


    public static class ShapeResult {
        public double distance = 0;
        public Point[] points = null;
    }


}


