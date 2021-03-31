package it.overside.distfinder;


import android.app.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SizeF;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import it.overside.distfinder.image.ImageUtils;
import it.overside.distfinder.image.ShapeDetect;


public class CameraActivity extends Activity implements OnTouchListener, CvCameraViewListener2 {

    private static final double WIDTH = 211;

    private static final String TAG = "CameraActivity";


    private Mat mRgba;
    private ShapeDetect mShapeDetect;
    private JavaCameraView mOpenCvCameraView;

    private final Handler mHandler = new Handler();

    private final Scalar colorLine = new Scalar(0, 255, 0);
    private TextView measureLabel;
    private EditText mIpEdit;
    private Button mButtonSave;
    private View mIpView;
    private boolean mustElaborate = false;

    private ConfigManager manager;
    private SendDataTask.SendData data = new SendDataTask.SendData();


    public CameraActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.main);

        manager = new ConfigManager(this);
        mIpEdit = findViewById(R.id.myIpEditor);
        mButtonSave = findViewById(R.id.salvaButton);
        mIpView = findViewById(R.id.insertIp);

        // Salvaggio
        mButtonSave.setOnClickListener((v)->{
            String url = mIpEdit.getText().toString();
            manager.setConfigString("url",url);

            mIpView.setVisibility(View.GONE);
            mustElaborate = true;
        });

        Button ipShop = findViewById(R.id.configShow);
        ipShop.setOnClickListener((v)->{
            mIpView.setVisibility(View.VISIBLE);
            mustElaborate = false;
        });

        mOpenCvCameraView = findViewById(R.id.camera);
        measureLabel = findViewById(R.id.cameraMeasure);

        Button button = findViewById(R.id.send);
        button.setOnClickListener(v -> {
            String url = manager.getConfig("url",null);
            (new SendDataTask(url)).execute(data);

        });

        // Inizializza camera
        mOpenCvCameraView.setCameraIndex(JavaCameraView.CAMERA_ID_BACK);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setMaxFrameSize(ShapeDetect.MAX_WIDTH, ShapeDetect.MAX_HEIGHT);

        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        mOpenCvCameraView.setDisplayRotation(rotation);


        String url = manager.getConfig("url",null);
        if(url == null){
            mIpView.setVisibility(View.VISIBLE);
            mustElaborate = false;
        }else{
            mIpEdit.setText(url);
            mIpView.setVisibility(View.GONE);
            mustElaborate = true;
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }


    /**
     * Handle evento load opencv library
     */
    private final BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                Log.i(TAG, "OpenCV loaded successfully");
                mOpenCvCameraView.enableView();
                mOpenCvCameraView.setOnTouchListener(CameraActivity.this);
            } else {
                super.onManagerConnected(status);
            }
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }


    }


    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mShapeDetect = new ShapeDetect();
        mShapeDetect.init(width, height);
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }


    /**
     * Elaborazione frame della camera
     *
     * @param inputFrame frame
     * @return Mat
     */
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        if (mustElaborate && inputFrame.rgba().width() > 0) {
            mRgba = inputFrame.rgba();
            double imageWidth = mRgba.width();
            String focalLenght = mOpenCvCameraView.getFocalLength();
            SizeF sensorSize = mOpenCvCameraView.getSensorSize();
            data.sensorWidth = sensorSize.getWidth();
            data.focalLenght = Float.valueOf(focalLenght);
            data.imageWidth = imageWidth;

            ShapeDetect.ShapeResult result = mShapeDetect.findChess(inputFrame.gray());
            mHandler.post(() -> {
                if (result != null) {
                    data.chessWidth = result.distance;
                    double distance = ((data.focalLenght * WIDTH) * imageWidth) / (result.distance * data.sensorWidth);

                    measureLabel.setText(distance + "");
                } else {

                    measureLabel.setText("");
                }
            });
            if (result != null) {
                return ImageUtils.drawRectFromPoints(mRgba, result.points, colorLine);

            }
            return mRgba;
        }
        return inputFrame.rgba();

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }


}