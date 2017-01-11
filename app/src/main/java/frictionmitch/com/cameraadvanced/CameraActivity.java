package frictionmitch.com.cameraadvanced;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.app.ActionBar.LayoutParams;
import android.widget.ViewSwitcher.ViewFactory;

import static android.view.MotionEvent.INVALID_POINTER_ID;
//import static frictionmitch.com.cameraadvanced.R.mipmap.fart_shart;
//import static frictionmitch.com.cameraadvanced.R.mipmap.gut;


public class CameraActivity extends Activity implements PictureCallback, SurfaceHolder.Callback {

    public static final String EXTRA_CAMERA_DATA = "camera_data";

    private static final String KEY_IS_CAPTURING = "is_capturing";

    private int mActivePointerId = INVALID_POINTER_ID;

    final Handler handler = new Handler();

    private android.widget.RelativeLayout.LayoutParams layoutParams;

    private int screenWidth;

    // Create a string for the ImageView label
    private static final String IMAGEVIEW_TAG = "icon bitmap";

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
    public static int mSwapCount = 0;
    private Button mSwitchCamera;
    private BMSummary bmSummary;
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout;

    private float oldXValue;
    private float oldYValue;

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    private float mLastTouch;

    private ImageSwitcher mImageSwitcher;
    public static int mImageMain;
    private static int mImageSecondary;

    public static final String PREFERENCES = "Prefs";
    SharedPreferences mSharedPreferences;


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

//    private OnClickListener mHideImageButtonClickListener = new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            hideFartButton();
//        }
//    };

    private OnClickListener mSwapImageClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
//            exitIcon();
//            mStomachImageButton.clearAnimation();
//            mStomachImageButton.setVisibility(View.INVISIBLE);
//            mStomachImageButton.setImageBitmap(null);
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




    //TODO: set stomach button to drag (code below)

    private View.OnDragListener mFrameLayoutDragListener = (new View.OnDragListener() {
       @Override
        public boolean onDrag(View v, DragEvent event) {
           final int action = event.getAction();

           switch (action) {
               case DragEvent.ACTION_DRAG_STARTED:
                   break;
               case DragEvent.ACTION_DRAG_EXITED:
                   break;
               case DragEvent.ACTION_DRAG_ENTERED:
                   break;
               case DragEvent.ACTION_DROP: {
//                   failure++;
                   return(true);
               }

               case DragEvent.ACTION_DRAG_ENDED:{
//                   total = total +1;
//                   int suc = total - failure;
//                   sucess.setText("Sucessful Drops :"+suc);
//                   text.setText("Total Drops: "+total);
                   return(true);
               }
               default:
                   break;
           }
           return true;
       }
    });

    private View.OnTouchListener mStomachButtonTouchListener = (new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent arg1) {
            // TODO Auto-generated method stub
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadow = new View.DragShadowBuilder(mStomachImageButton);
            v.startDrag(data, shadow, null, 0);
            return false;
        }
    });

    private View.OnLongClickListener mStomachButtonLongClickListener = (new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    });

    private OnTouchListener mFrameLayoutTouchListener = (new OnTouchListener() {
        @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    oldXValue = event.getX();
                    oldYValue = event.getY();
                    Log.i("Omid", "Action Down " + oldXValue + "," + oldYValue);
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    FrameLayout fl = (FrameLayout) findViewById(R.id.camera_frame);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(v.getWidth(), v.getHeight());
                    params.leftMargin = (int) (event.getRawX() - (v.getWidth() / 2));
                    params.topMargin = (int) (event.getRawY() - (v.getHeight()));
//                    v.getHeight(), (int) (event.getRawX() - (v.getWidth() / 2)), (int) (event.getRawY() - (v.getHeight()));
                    v.setLayoutParams(params);
                }
                return true;
            }
    });

    private View.OnDragListener mStomachButtonDragListener = (new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            // TODO Auto-generated method stub
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadow = new View.DragShadowBuilder(mStomachImageButton);
            v.startDrag(data, shadow, null, 0);
            return false;
        }
    });


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
            }, 4000);

        }
    };

    private OnClickListener mImageSwitcherClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            scale();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    switch (mImageMain) {
                        case R.mipmap.gut:
                            Intent intent = new Intent(CameraActivity.this, BMSummary.class);
                            CameraActivity.this.startActivity(intent);
                            break;
                        case R.mipmap.fart_shart:
                            Intent intent1 = new Intent(CameraActivity.this, FartShart.class);
                            CameraActivity.this.startActivity(intent1);
                            break;
                    }
                    mCamera.release();
                    mCamera = null;
                }
            }, 4000);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera);

        mImageMain = R.mipmap.gut;
        mImageSecondary = R.mipmap.fart_shart;

        mCameraImage = (ImageView) findViewById(R.id.camera_image_view);
        mCameraImage.setVisibility(View.INVISIBLE);

        mCameraPreview = (SurfaceView) findViewById(R.id.preview_view);
        final SurfaceHolder surfaceHolder = mCameraPreview.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // Click on the icon to do something...
        mSwitchCamera = (Button) findViewById(R.id.switch_camera);
        mSwitchCamera.setOnClickListener(mSwitchCameraButtonClickListener);

        mFrameLayout = (FrameLayout)findViewById(R.id.camera_frame);
//        mFrameLayout.setOnTouchListener(mFrameLayoutTouchListener);

//        mRelativeLayout = (RelativeLayout)findViewById(R.id.relative_layout);


//        mStomachImageButton = (ImageButton) findViewById(R.id.stomachButton);
//        mStomachImageButton.setImageBitmap(null);
//        mStomachImageButton.setVisibility(View.INVISIBLE);
//        mStomachImageButton.setOnClickListener(mStomachButtonClickListener);
//        mStomachImageButton.setOnLongClickListener(mStomachButtonLongClickListener);
//        mStomachImageButton.setOnDragListener(mStomachButtonDragListener);
//        mStomachImageButton.setOnTouchListener(mStomachButtonTouchListener);

//        mStomachImageButton.setOnClickListener(mHideImageButtonClickListener);

        mFartImageButton = (ImageButton) findViewById(R.id.fartButton);
        mFartImageButton.setOnClickListener(mSwapImageClickListener);


        // Click on "Snap Picure" button to do something...
//        mCaptureImageButton = (Button) findViewById(R.id.capture_image_button);
//        mCaptureImageButton.setOnClickListener(mCaptureImageButtonClickListener);

        final Button doneButton = (Button) findViewById(R.id.done_button);
        doneButton.setOnClickListener(mDoneButtonClickListener);

        mIsCapturing = true;
//        introduceIcon();
        mImageSwitcher = (ImageSwitcher)findViewById(R.id.imageSwitcher);
        mImageSwitcher.setOnClickListener(mImageSwitcherClickListener);
//        mImageSwitcher.setImageResource(R.mipmap.gut);

        mImageSwitcher.setFactory(new ViewFactory() {
            @Override
            public View makeView() {
                ImageView view = new ImageView(getApplicationContext());
                view.setScaleType(ImageView.ScaleType.CENTER);
                view.setLayoutParams(new
                        ImageSwitcher.LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT));
//                view.setImageResource(R.mipmap.gut);
                return view;

            }
        });
//        mImageSwitcher.clearAnimation();
        mImageSwitcher.setInAnimation(this, R.anim.slide);
        mImageSwitcher.setOutAnimation(this, R.anim.slide_reverse);
//        mImageSwitcher.setImageResource(R.mipmap.gut);
        mImageSwitcher.setImageResource(mImageMain);

//        swapImageButton();
//        mImageSwitcher.setVisibility(View.VISIBLE);
//        mImageSwitcher.setImageResource(R.mipmap.gut);
//        mImageSwitcher.setInAnimation(this, R.anim.slide);
//        introduceIcon();
//        mImageSwitcher.setInAnimation(this, R.anim.slide);

//        setupImageDisplay();

//        callCamera();

//        mCamera.setDisplayOrientation(90);Animation animationOut = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
//        Animation animationIn = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
//        Animation animationOut = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
//
//        mImageSwitcher.setImageResource(R.mipmap.gut);
//        mImageSwitcher.setInAnimation(animationIn);
//        mImageSwitcher.setOutAnimation(animationOut);
    }



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
//            mStomachImageButton.setScaleX(2f);
//            mStomachImageButton.setScaleY(1.5f);
            mImageSwitcher.setScaleX(2f);
            mImageSwitcher.setScaleY(2f);
//            getCenter();
        } else {
//            mStomachImageButton.setScaleX(4);
//            mStomachImageButton.setScaleY(4);
            mImageSwitcher.setScaleX(4);
            mImageSwitcher.setScaleY(4);
//            mStomachImageButton.setPadding(0, 0, 0, pixelsToDp(120));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        loadImageState();
//        mStomachImageButton.clearAnimation();
//        introduceIcon();
        callCamera();
        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();
//        mImageSwitcher.setImageResource(R.mipmap.gut);
        mImageSwitcher.setInAnimation(this, R.anim.slide);
        mImageSwitcher.setOutAnimation(this, R.anim.slide_reverse);


    }

    public void saveImageState() {
        int getImageMain = mImageMain;
        int getImageSecondary = mImageSecondary;
//        SharedPreferences.Editor editor = mSharedPreferences.edit();

        SharedPreferences sp = getSharedPreferences(PREFERENCES, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("main", getImageMain);
        editor.putInt("secondary", getImageSecondary);
        editor.commit();
    }

    public void loadImageState() {
        //--Return values saved in the SharedPreferences--
//        mSharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
////        int restoredMain = getSharedPreferences("main", )
//
//        SharedPreferences mSharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
//        String restoredText = mSharedPreferences.getString("text", null);
//        if (restoredText != null) {
//            String name = mSharedPreferences.getString("name", "No name defined");//"No name defined" is the default value.
//            int main = mSharedPreferences.getInt("main", 0); //0 is the default value.
//            int secondary = mSharedPreferences.getInt("secondary", 0);
//        }

        SharedPreferences sp = getSharedPreferences(PREFERENCES, Activity.MODE_PRIVATE);
//            String restoredText = mSharedPreferences.getString("text", null);
            int restoredMain = sp.getInt("main", -1);
            if (restoredMain != -1) {
                int main = mSharedPreferences.getInt("main", 0); //0 is the default value.
                int secondary = mSharedPreferences.getInt("secondary", 0);
            }
//        int myIntValue = sp.getInt("your_int_key", -1);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mImageSwitcher.clearAnimation();

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
        saveImageState();
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

    public void hideImageButton() {
        if(mStomachImageButton.isShown()) {
        mStomachImageButton.setVisibility(View.INVISIBLE);
        } else {
            mStomachImageButton.setVisibility(View.VISIBLE);
        }
    }

    public void swapImageButton() {
//        exitIcon();
//        mStomachImageButton.setImageBitmap(null);
        mSwapCount++;
        if(mSwapCount % 2 == 0) {
//            mStomachImageButton.setBackground(getDrawable(gut));
            mImageMain = R.mipmap.gut;
            mImageSecondary = R.mipmap.fart_shart;
//            mFartImageButton.setBackground(getDrawable(R.mipmap.fart_shart));
//            mStomachImageButton.setVisibility(View.VISIBLE);
//            mImageSwitcher.setImageResource(R.mipmap.gut);
        } else {
            mImageMain = R.mipmap.fart_shart;
            mImageSecondary = R.mipmap.gut;
//            mStomachImageButton.setBackground(getDrawable(fart_shart));
//            mFartImageButton.setBackground(getDrawable(mImageMain));
//            mFartImageButton.setBackground(getDrawable(R.mipmap.gut));
//            mStomachImageButton.setVisibility(View.VISIBLE);
//            mImageSwitcher.setImageResource(mImageSecondary);
//            mImageSwitcher.setImageResource(R.mipmap.fart_shart);
//        mImageSwitcher.setInAnimation(this, R.anim.slide);
        }
            mFartImageButton.setBackground(getDrawable(mImageSecondary));
//        mImageSwitcher.setOutAnimation(this, R.anim.slide_reverse);
            mImageSwitcher.setImageResource(mImageMain);

        saveImageState();
//        introduceIcon();
        mImageSwitcher.setInAnimation(this, R.anim.slide);

    }

//    public void hideFartButton() {
//        if(mFartImageButton.isShown()) {
//            mFartImageButton.setVisibility(View.INVISIBLE);
//        } else {
//            mFartImageButton.setVisibility(View.VISIBLE);
//        }
//    }

    public void getCenter() {

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        int DeviceTotalWidth = metrics.widthPixels;
        int DeviceTotalHeight = metrics.heightPixels;

//        LinearLayout LinearLayoutImageCenter=(LinearLayout) findViewById(R.id.linear_layout);
        FrameLayout FrameLayoutImageCenter=(FrameLayout) findViewById(R.id.camera_frame);
        mStomachImageButton.setPadding(DeviceTotalWidth/4,DeviceTotalHeight/4,0,0);
    }

    public void scale() {
//        mStomachImageButton.clearAnimation();
        mImageSwitcher.clearAnimation();
//        mStomachImageButton = (ImageButton)findViewById(R.id.stomachButton);

//        if(mSwapCount % 2 == 0) {
////            mStomachImageButton.setImageResource(gut);
//            mImageSwitcher.setImageResource(gut);
//        } else {
////            mStomachImageButton.setImageResource(fart_shart);
//            mImageSwitcher.setImageResource(fart_shart);
//        }

        Animation scaleAnimation = AnimationUtils.loadAnimation(
                getApplicationContext(), R.anim.scale_animation);

        Animation reverseScaleAnimation = AnimationUtils.loadAnimation(
                getApplicationContext(), R.anim.reverse_scale_animation);

        Animation blinkAnimation = AnimationUtils.loadAnimation(
                getApplicationContext(), R.anim.blink);

        Animation fadeAnimation = AnimationUtils.loadAnimation(
                getApplicationContext(), R.anim.fade);

        Animation rotateAnimation = AnimationUtils.loadAnimation(
                getApplicationContext(), R.anim.rotate);

        Animation slideAnimation = AnimationUtils.loadAnimation(
                getApplicationContext(), R.anim.slide);



        AnimationSet scale = new AnimationSet(false);
        scale.addAnimation(scaleAnimation);
//        scale.addAnimation(slideAnimation);
//        scale.addAnimation(rotateAnimation);
//        scale.addAnimation(blinkAnimation);
//        scale.addAnimation(fadeAnimation);
//        scale.addAnimation(reverseScaleAnimation);
//        mStomachImageButton.startAnimation(scale);
        mImageSwitcher.startAnimation(scale);
    }

    public void introduceIcon() {
//        mStomachImageButton.setVisibility(View.INVISIBLE);
//        mStomachImageButton.clearAnimation();
        Animation slideAnimation = AnimationUtils.loadAnimation(
                getApplicationContext(), R.anim.slide);
//        Animation reverseSlideAnimation = AnimationUtils.loadAnimation(
//                getApplicationContext(), R.anim.slide_reverse);

        AnimationSet slide = new AnimationSet(false);
        slide.addAnimation(slideAnimation);
//        slide.addAnimation(reverseSlideAnimation);
//        mStomachImageButton.startAnimation(slide);
        mImageSwitcher.startAnimation(slide);
    }

    public void animations() {
        Animation in = AnimationUtils.loadAnimation(this,android.R.anim.slide_in_left);
        mImageSwitcher.setInAnimation(in);
//        mImageSwitcher.setOutAnimation(out);
    }

    public void exitIcon(){
//        mStomachImageButton.clearAnimation();
//        mStomachImageButton.setVisibility(View.INVISIBLE);
//        Animation slideAnimation = AnimationUtils.loadAnimation(
//                getApplicationContext(), R.anim.slide);
        Animation reverseSlideAnimation = AnimationUtils.loadAnimation(
                getApplicationContext(), R.anim.slide_reverse);

        AnimationSet reverse = new AnimationSet(false);
        reverse.addAnimation(reverseSlideAnimation);
//        slide.addAnimation(reverseSlideAnimation);
        mStomachImageButton.startAnimation(reverse);
//        onAnimationEnd(reverse);
//        introduceIcon();
    }

    public void onAnimationEnd(Animation animation) {


        Animation slideAnimation = AnimationUtils.loadAnimation(
                getApplicationContext(), R.anim.slide);
        AnimationSet slide = new AnimationSet(false);
        slide.addAnimation(slideAnimation);
        mStomachImageButton.startAnimation(slide);
//        introduceIcon();
    }








//    private class ScaleListener
//            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//        @Override
//        public boolean onScale(ScaleGestureDetector detector) {
//            mScaleFactor *= detector.getScaleFactor();
//
//            // Don't let the object get too small or too large.
//            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
//
//            invalidate();
//            return true;
//        }
//    }

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

    // The ‘active pointer’ is the one currently moving our object.




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
