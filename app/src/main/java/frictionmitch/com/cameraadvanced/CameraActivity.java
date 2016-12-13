package frictionmitch.com.cameraadvanced;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


public class CameraActivity extends Activity implements PictureCallback, SurfaceHolder.Callback {

    public static final String EXTRA_CAMERA_DATA = "camera_data";

    private static final String KEY_IS_CAPTURING = "is_capturing";

    final Handler handler = new Handler();

    private int screenWidth;

    private Camera mCamera;
    private ImageView mCameraImage;
    private SurfaceView mCameraPreview;
    private Button mCaptureImageButton;
    private ImageButton mStomachImageButton;
    private ImageButton mFartImageButton;
    private byte[] mCameraData;
    private boolean mIsCapturing;
    private float mDist;
    private int switchCount = 1;
    private Button mSwitchCamera;
    private BMSummary bmSummary;
//    private SurfaceHolder mHolder;


    private OnClickListener mCaptureImageButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            captureImage();

        }
    };

    private OnClickListener mRecaptureImageButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            setupImageCapture();
        }
    };

    private OnClickListener mDoneButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mCameraData != null) {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_CAMERA_DATA, mCameraData);
                setResult(RESULT_OK, intent);
            } else {
                setResult(RESULT_CANCELED);
            }
            finish();
        }
    };

    private OnClickListener mHideImageButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            hideFartButton();
        }
    };

    private OnClickListener mHideFartImageButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            swapImageButton();
        }
    };

    private OnClickListener mSwitchCameraButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onPause();

            switchCount++;
            switchCount = switchCount % 2;
            callCamera();
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
        }
    };

    private OnClickListener mStomachButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            scale();
//            reverseScale();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms

            Intent myIntent = new Intent(CameraActivity.this, BMSummary.class);
            CameraActivity.this.startActivity(myIntent);
            mCamera.release();
            mCamera = null;
                }
            }, 2000);

        }
    };

    public int pixelsToDp(int dp) {
        int padding_in_dp = dp;  // 6 dps
        final float scale = getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
        return padding_in_px;
    }



//    public CameraActivity(Camera camera) {
////        super(context);
//
//        mCamera = camera;
//        mCamera.setDisplayOrientation(90);
//        //get the holder and set this class as the callback, so we can get camera data here
//        mHolder = getHolder();
//        mHolder.addCallback(this);
//        mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera);

        mCameraImage = (ImageView) findViewById(R.id.camera_image_view);
        mCameraImage.setVisibility(View.INVISIBLE);

        mCameraPreview = (SurfaceView) findViewById(R.id.preview_view);
        final SurfaceHolder surfaceHolder = mCameraPreview.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // Click on the icon to do something...
        mSwitchCamera = (Button) findViewById(R.id.switch_camera);
        mSwitchCamera.setOnClickListener(mSwitchCameraButtonClickListener);

        mStomachImageButton = (ImageButton) findViewById(R.id.stomachButton);
        mStomachImageButton.setOnClickListener(mStomachButtonClickListener);
//        mStomachImageButton.setOnClickListener(mHideImageButtonClickListener);

        mFartImageButton = (ImageButton) findViewById(R.id.fartButton);
        mFartImageButton.setOnClickListener(mHideFartImageButtonClickListener);


        // Click on "Snap Picure" button to do something...
        mCaptureImageButton = (Button) findViewById(R.id.capture_image_button);
        mCaptureImageButton.setOnClickListener(mCaptureImageButtonClickListener);

        final Button doneButton = (Button) findViewById(R.id.done_button);
        doneButton.setOnClickListener(mDoneButtonClickListener);

        mIsCapturing = true;

//        setupImageDisplay();

//        callCamera();

//        mCamera.setDisplayOrientation(90);
    }

//    public CameraView(Context context, Camera camera) {
//        super(context);
//
//        mCamera = camera;
//        mCamera.setDisplayOrientation(90);
//        //get the holder and set this class as the callback, so we can get camera data here
//        mHolder = getHolder();
//        mHolder.addCallback(this);
//        mHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
//    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putBoolean(KEY_IS_CAPTURING, mIsCapturing);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mIsCapturing = savedInstanceState.getBoolean(KEY_IS_CAPTURING, mCameraData == null);
        if (mCameraData != null) {
            setupImageDisplay();
        } else {
            setupImageCapture();
        }
    }

    public void callCamera() {
        if (mCamera == null) {
            try {
                mCamera = Camera.open(switchCount);
                mCamera.setPreviewDisplay(mCameraPreview.getHolder());
                if (mIsCapturing) {
                    mCamera.startPreview();
                }
            } catch (Exception e) {
                Toast.makeText(CameraActivity.this, "Unable to open camera.", Toast.LENGTH_LONG)
                        .show();
            }
        }
        if (switchCount % 2 == 0) {
//            mStomachImageButton.setPadding(0, 0, 0, pixelsToDp(200));
//            mStomachImageButton.setLayoutParams();
            mStomachImageButton.setScaleX(2f);
            mStomachImageButton.setScaleY(1.5f);
//            getCenter();
        } else {
            mStomachImageButton.setScaleX(5);
            mStomachImageButton.setScaleY(4);
//            mStomachImageButton.setPadding(0, 0, 0, pixelsToDp(120));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        callCamera();
        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        mCameraData = data;
        setupImageDisplay();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(holder);
                if (mIsCapturing) {

                        mCamera.setDisplayOrientation(90);
                        mCamera.startPreview();

                }
            } catch (IOException e) {
                Toast.makeText(CameraActivity.this, "Unable to start camera preview.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    private void captureImage() {
        mCamera.takePicture(null, null, this);
    }

    private void setupImageCapture() {
        mCameraImage.setVisibility(View.INVISIBLE);
//        mCamera.setDisplayOrientation(90);
        mCameraPreview.setVisibility(View.VISIBLE);
//        mCameraPreview.setScaleX(1.5f);
        mCamera.startPreview();
        mCamera.setDisplayOrientation(90);
        mCamera.startFaceDetection();
//        mCamera.setFaceDetectionListener();
//        mCamera.setZoomChangeListener();
        mCaptureImageButton.setText(R.string.capture_image);
        mCaptureImageButton.setOnClickListener(mCaptureImageButtonClickListener);
        screenWidth = mCameraPreview.getWidth();
    }

    private void setupImageDisplay() {
        Bitmap bitmap = BitmapFactory.decodeByteArray(mCameraData, 0, mCameraData.length);
//        mCamera.setDisplayOrientation(90);
        mCameraImage.setImageBitmap(bitmap);
        mCamera.stopPreview();
        mCameraPreview.setVisibility(View.INVISIBLE);
//        mCameraPreview.setMinimumWidth(screenWidth);
        mCameraImage.setRotation(270);
        mCameraImage.setScaleY(-1.f);
//        mCameraImage.setMinimumWidth(mCameraPreview.getWidth());
        mCameraImage.setVisibility(View.VISIBLE);
        mCaptureImageButton.setText(R.string.recapture_image);
        mCaptureImageButton.setOnClickListener(mRecaptureImageButtonClickListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Get the pointer ID
        Camera.Parameters params = mCamera.getParameters();
        int action = event.getAction();


        if (event.getPointerCount() > 1) {
            // handle multi-touch events
            if (action == MotionEvent.ACTION_POINTER_DOWN) {
                mDist = getFingerSpacing(event);
            } else if (action == MotionEvent.ACTION_MOVE && params.isZoomSupported()) {
                mCamera.cancelAutoFocus();
                handleZoom(event, params);
            }
        } else {
            // handle single touch events
            if (action == MotionEvent.ACTION_UP) {
                handleFocus(event, params);
            }
        }
        return true;
    }

    private void handleZoom(MotionEvent event, Camera.Parameters params) {
        int maxZoom = params.getMaxZoom();
//        int minZoom = params.getMinZoom();
        int zoom = params.getZoom();
        float newDist = getFingerSpacing(event);
        if (newDist > mDist) {
            //zoom in
            if (zoom < maxZoom)
                zoom++;
        } else if (newDist < mDist) {
            //zoom out
            if (zoom > 0)
                zoom--;
        }
        mDist = newDist;
        params.setZoom(zoom);
        mCamera.setParameters(params);
    }

    public void handleFocus(MotionEvent event, Camera.Parameters params) {
        int pointerId = event.getPointerId(0);
        int pointerIndex = event.findPointerIndex(pointerId);
        // Get the pointer's current position
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);

        List<String> supportedFocusModes = params.getSupportedFocusModes();
        if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, Camera camera) {
                    // currently set to auto-focus on single touch
                }
            });
        }
    }

    /**
     * Determine the space between the first two fingers
     */
    private float getFingerSpacing(MotionEvent event) {
        // ...
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    public void swapImageButton() {
        if(mStomachImageButton.isShown()) {
        mStomachImageButton.setVisibility(View.INVISIBLE);
        } else {
            mStomachImageButton.setVisibility(View.VISIBLE);
        }
    }

    public void hideFartButton() {
        if(mFartImageButton.isShown()) {
            mFartImageButton.setVisibility(View.INVISIBLE);
        } else {
            mFartImageButton.setVisibility(View.VISIBLE);
        }
    }

    public void scaleImage() {

    }

    public void getCenter() {

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        int DeviceTotalWidth = metrics.widthPixels;
        int DeviceTotalHeight = metrics.heightPixels;

        LinearLayout LinearLayoutImageCenter=(LinearLayout) findViewById(R.id.linear_layout);
        mStomachImageButton.setPadding(DeviceTotalWidth/4,DeviceTotalHeight/4,0,0);
    }

    public void scale() {
        mStomachImageButton = (ImageButton)findViewById(R.id.stomachButton);
        mStomachImageButton.setImageResource(R.mipmap.gut);
        mStomachImageButton.clearAnimation();

        Animation scaleAnimation = AnimationUtils.loadAnimation(
                getApplicationContext(), R.anim.scale_animation);

        Animation reverseScaleAnimation = AnimationUtils.loadAnimation(
                getApplicationContext(), R.anim.reverse_scale_animation);


        AnimationSet scale = new AnimationSet(false);
        scale.addAnimation(scaleAnimation);
        scale.addAnimation(reverseScaleAnimation);
        mStomachImageButton.startAnimation(scale);
    }

//    public void reverseScale() {
//        mStomachImageButton = (ImageButton)findViewById(R.id.stomachButton);
//        mStomachImageButton.setImageResource(R.mipmap.gut);
//        mStomachImageButton.clearAnimation();
//
//        Animation scaleAnimation = AnimationUtils.loadAnimation(
//                getApplicationContext(), R.anim.reverse_scale_animation);
////        mStomachImageButton.setAnimation(scaleAnimation);
//        mStomachImageButton.startAnimation(scaleAnimation);
//
//        AnimationSet s = new AnimationSet(false);//false means don't share interpolators
//        s.addAnimation(traintween);
//        s.addAnimation(trainfad);
//        mytrain.startAnimation(s);
//    }
}
//
//public class CameraActivity extends SurfaceView implements SurfaceHolder.Callback {
//
//    private static final String TAG = "CameraPreview";
//    public static final String EXTRA_CAMERA_DATA = "camera_data";
//
//    private Context mContext;
//    private SurfaceHolder mHolder;
//    private Camera mCamera;
//    private List<Camera.Size> mSupportedPreviewSizes;
//    private Camera.Size mPreviewSize;
//
//    private static final String KEY_IS_CAPTURING = "is_capturing";
//    private int screenWidth;
//
//    private ImageView mCameraImage;
//    private SurfaceView mCameraPreview;
//    private Button mCaptureImageButton;
//    private byte[] mCameraData;
//    private boolean mIsCapturing;
//    private float mDist;
//
//    public CameraActivity(Context context, Camera camera) {
//        super(context);
//        mContext = context;
//        mCamera = camera;
//
//        // supported preview sizes
//        mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
//        for(Camera.Size str: mSupportedPreviewSizes)
//            Log.e(TAG, str.width + "/" + str.height);
//
//        // Install a SurfaceHolder.Callback so we get notified when the
//        // underlying surface is created and destroyed.
//        mHolder = getHolder();
//        mHolder.addCallback(this);
//        // deprecated setting, but required on Android versions prior to 3.0
//        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//    }
//
//    public void surfaceCreated(SurfaceHolder holder) {
//        // empty. surfaceChanged will take care of stuff
//    }
//
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        // empty. Take care of releasing the Camera preview in your activity.
//    }
//
//    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
//        Log.e(TAG, "surfaceChanged => w=" + w + ", h=" + h);
//        // If your preview can change or rotate, take care of those events here.
//        // Make sure to stop the preview before resizing or reformatting it.
//        if (mHolder.getSurface() == null){
//            // preview surface does not exist
//            return;
//        }
//
//        // stop preview before making changes
//        try {
//            mCamera.stopPreview();
//        } catch (Exception e){
//            // ignore: tried to stop a non-existent preview
//        }
//
//        // set preview size and make any resize, rotate or reformatting changes here
//        // start preview with new settings
//        try {
//            Camera.Parameters parameters = mCamera.getParameters();
//            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
//            mCamera.setParameters(parameters);
//            mCamera.setDisplayOrientation(90);
//            mCamera.setPreviewDisplay(mHolder);
//            mCamera.startPreview();
//
//        } catch (Exception e){
//            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
//        }
//    }
//
////    @Override
////    public void onPictureTaken(byte[] data, Camera camera) {
////        mCameraData = data;
////        setupImageDisplay();
////    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
//        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
//
//        if (mSupportedPreviewSizes != null) {
//            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
//        }
//
//        float ratio;
//        if(mPreviewSize.height >= mPreviewSize.width)
//            ratio = (float) mPreviewSize.height / (float) mPreviewSize.width;
//        else
//            ratio = (float) mPreviewSize.width / (float) mPreviewSize.height;
//
//        // One of these methods should be used, second method squishes preview slightly
//        setMeasuredDimension(width, (int) (width * ratio));
////        setMeasuredDimension((int) (width * ratio), height);
//    }
//
//    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
//        final double ASPECT_TOLERANCE = 0.1;
//        double targetRatio = (double) h / w;
//
//        if (sizes == null)
//            return null;
//
//        Camera.Size optimalSize = null;
//        double minDiff = Double.MAX_VALUE;
//
//        int targetHeight = h;
//
//        for (Camera.Size size : sizes) {
//            double ratio = (double) size.height / size.width;
//            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
//                continue;
//
//            if (Math.abs(size.height - targetHeight) < minDiff) {
//                optimalSize = size;
//                minDiff = Math.abs(size.height - targetHeight);
//            }
//        }
//
//        if (optimalSize == null) {
//            minDiff = Double.MAX_VALUE;
//            for (Camera.Size size : sizes) {
//                if (Math.abs(size.height - targetHeight) < minDiff) {
//                    optimalSize = size;
//                    minDiff = Math.abs(size.height - targetHeight);
//                }
//            }
//        }
//
//        return optimalSize;
//    }
//
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        // Get the pointer ID
//        Camera.Parameters params = mCamera.getParameters();
//        int action = event.getAction();
//
//
//        if (event.getPointerCount() > 1) {
//            // handle multi-touch events
//            if (action == MotionEvent.ACTION_POINTER_DOWN) {
//                mDist = getFingerSpacing(event);
//            } else if (action == MotionEvent.ACTION_MOVE && params.isZoomSupported()) {
//                mCamera.cancelAutoFocus();
//                handleZoom(event, params);
//            }
//        } else {
//            // handle single touch events
//            if (action == MotionEvent.ACTION_UP) {
//                handleFocus(event, params);
//            }
//        }
//        return true;
//    }
//
//    private void handleZoom(MotionEvent event, Camera.Parameters params) {
//        int maxZoom = params.getMaxZoom();
//        int zoom = params.getZoom();
//        float newDist = getFingerSpacing(event);
//        if (newDist > mDist) {
//            //zoom in
//            if (zoom < maxZoom)
//                zoom++;
//        } else if (newDist < mDist) {
//            //zoom out
//            if (zoom > 0)
//                zoom--;
//        }
//        mDist = newDist;
//        params.setZoom(zoom);
//        mCamera.setParameters(params);
//    }
//
//    public void handleFocus(MotionEvent event, Camera.Parameters params) {
//        int pointerId = event.getPointerId(0);
//        int pointerIndex = event.findPointerIndex(pointerId);
//        // Get the pointer's current position
//        float x = event.getX(pointerIndex);
//        float y = event.getY(pointerIndex);
//
//        List<String> supportedFocusModes = params.getSupportedFocusModes();
//        if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
//            mCamera.autoFocus(new Camera.AutoFocusCallback() {
//                @Override
//                public void onAutoFocus(boolean b, Camera camera) {
//                    // currently set to auto-focus on single touch
//                }
//            });
//        }
//    }
//
//    private float getFingerSpacing(MotionEvent event) {
//        // ...
//        float x = event.getX(0) - event.getX(1);
//        float y = event.getY(0) - event.getY(1);
//        return (float) Math.sqrt(x * x + y * y);
//    }
//
////    private void captureImage() {
////        mCamera.takePicture(null, null, this);
////    }
//
//    private void setupImageCapture() {
//        mCameraImage.setVisibility(View.INVISIBLE);
////        mCamera.setDisplayOrientation(90);
//        mCameraPreview.setVisibility(View.VISIBLE);
////        mCameraPreview.setScaleX(1.5f);
//        mCamera.startPreview();
//        mCamera.setDisplayOrientation(90);
//        mCamera.startFaceDetection();
////        mCamera.setFaceDetectionListener();
////        mCamera.setZoomChangeListener();
//        mCaptureImageButton.setText(R.string.capture_image);
////        mCaptureImageButton.setOnClickListener(mCaptureImageButtonClickListener);
//        screenWidth = mCameraPreview.getWidth();
//    }
//
//    private void setupImageDisplay() {
//        Bitmap bitmap = BitmapFactory.decodeByteArray(mCameraData, 0, mCameraData.length);
////        mCamera.setDisplayOrientation(90);
//        mCameraImage.setImageBitmap(bitmap);
//        mCamera.stopPreview();
//        mCameraPreview.setVisibility(View.INVISIBLE);
////        mCameraPreview.setMinimumWidth(screenWidth);
//        mCameraImage.setRotation(270);
//        mCameraImage.setScaleY(-1.f);
////        mCameraImage.setMinimumWidth(mCameraPreview.getWidth());
//        mCameraImage.setVisibility(View.VISIBLE);
//        mCaptureImageButton.setText(R.string.recapture_image);
////        mCaptureImageButton.setOnClickListener(mRecaptureImageButtonClickListener);
//    }
//
//
//}
