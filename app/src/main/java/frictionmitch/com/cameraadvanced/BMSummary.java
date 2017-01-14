package frictionmitch.com.cameraadvanced;


import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import android.*;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import static android.R.color.holo_red_dark;
import static frictionmitch.com.cameraadvanced.CameraActivity.mImageMain;
import static frictionmitch.com.cameraadvanced.CameraActivity.mSwapCount;

public class BMSummary extends Activity {

//    private static final int TAKE_PICTURE_REQUEST_B = 100;

    private Button mBackButton;
    private ImageButton mBackImageButton;
    private TextView mSummary;
    private TextView mSummaryTextView;
    private TextView mRandomTextView;
    private TextView mCountdownTextView;
    private TextView mExpiryTextView;
    private TextView mGirthTextView;
    private TextView mAirQualityTextView;
    private TextView mWeightTextView;
    private TextView mFlushesTextView;
    private TextView mViscosityTextView;
    private Button mMapButton;
    private CountDownTimer mCountDownTimer;
    private int mTime;
    private String mTimer;
    private long mDurationSeconds;

    //--for GPS(Locations Activity)--
    final private int REQUEST_COURSE_ACCESS = 123;
    boolean permissionGranted = false;
    LocationManager mLocationManager;
    LocationListener mLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        mBackButton = (Button) findViewById(R.id.back_button);
        mBackButton.setOnClickListener(mBackButtonClickListener);


        mSummary = (TextView)findViewById(R.id.summary);

        mSummaryTextView = (TextView)findViewById(R.id.bottomLineTextView);
        mSummaryTextView.setText(randomText(this.getResources().getStringArray(R.array.bottomline)));

//        mRandomTextView = (TextView)findViewById(R.id.countdownTextView);
//        mRandomTextView.setText(randomCountdown());

        mCountdownTextView = (TextView)findViewById(R.id.countdownTextView);
        mCountdownTextView.setText(randomText(this.getResources().getStringArray(R.array.countdown)));

        mExpiryTextView = (TextView)findViewById(R.id.expiryTextView);
        mExpiryTextView.setText(randomText(this.getResources().getStringArray(R.array.expiry)));

        mGirthTextView = (TextView)findViewById(R.id.girthTextView);
        mGirthTextView.setText(randomText(this.getResources().getStringArray(R.array.girth)));

        mAirQualityTextView = (TextView)findViewById(R.id.airQualityTextView);
        mAirQualityTextView.setText(randomText(this.getResources().getStringArray(R.array.air)));

        mWeightTextView = (TextView)findViewById(R.id.weightTextView);
        mWeightTextView.setText(randomText(this.getResources().getStringArray(R.array.weight)));

        mFlushesTextView = (TextView)findViewById(R.id.flushesTextView);
        mFlushesTextView.setText(randomText(this.getResources().getStringArray(R.array.flushes)));

        mViscosityTextView = (TextView)findViewById(R.id.viscosityTextView);
        mViscosityTextView.setText(randomText(this.getResources().getStringArray(R.array.viscosity)));



        mMapButton = (Button)findViewById(R.id.mapButton);
        mMapButton.setOnClickListener(mapButtonClickListener);

        mTimer = mCountdownTextView.getText().toString();

        mTime = Integer.parseInt(mCountdownTextView.getText().toString())*1000;
//        mDouble = mTime * 1000;

//        mTimer = String.format("%02d:%02d:%02d", mTime / 3600,
//                (mTime % 3600) / 60, (mTime % 60));


//        String.format("%02d:%02d:%02d", durationMinutes / 60, durationMinutes % 60, 0);

        mCountDownTimer = new CountDownTimer(mTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mDurationSeconds = millisUntilFinished/1000;
//                int hours = (int)TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
//                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
//                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);

//                mCountdownTextView.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
                mCountdownTextView.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d",
                        mDurationSeconds / 3600,
                        (mDurationSeconds % 3600) / 60, (mDurationSeconds % 60)));
                if(mDurationSeconds <= 600) {
                    mCountdownTextView.setTextColor(getColor(android.R.color.holo_red_dark));
                    mCountdownTextView.setTextSize(30);
                    mMapButton.setBackgroundColor(getColor(android.R.color.holo_red_dark));
                    mMapButton.setText("Locate Toilet");
                    if (mDurationSeconds <= 500) {
                        mMapButton.setText("Buy New Underwear");
                        mMapButton.setBackgroundColor(Color.parseColor("#663300"));
                        mMapButton.setTextColor(Color.parseColor("#ffffff"));
                    }
                } else {
                    mCountdownTextView.setTextColor(getColor(android.R.color.holo_green_dark));
                    mCountdownTextView.setTextSize(20);
                    mMapButton.setBackgroundColor(getColor(android.R.color.holo_green_dark));
                    mMapButton.setText("Locate Toilet");
                }

//                time = (long)mTime;
//                int totalSeconds = (int)(millisUntilFinished / 1000);
//                int hours = totalSeconds / 3600;
//                int minutes = totalSeconds % 3600;
//                int seconds = totalSeconds % 60;

//                mCountdownTextView.setText("seconds remaining: " + millisUntilFinished / 1000);
//                mCountdownTextView.setText(String.format("Hours: %02d, Minutes: %02d, Seconds: %02d", hours, minutes, seconds));
//                mCountdownTextView.setText(String.format("%02d:%02d:%02d", time / 3600, (time % 3600) / 60, (time % 60)));
//                mCountdownTextView.setText("Time left: " + time/1000);
            }
            @Override
            public void onFinish() {
                mCountdownTextView.setText("Out of Time!");
            }
        }.start();

        //--Locations(Map Activity)--
        //--remove the location listener--
//        if(ActivityCompat.checkSelfPermission(this,
//
//                android.Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this,
//                        android.Manifest.permission.ACCESS_COARSE_LOCATION)
//                        != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[] {
//                            android.Manifest.permission.ACCESS_COARSE_LOCATION
//                    },
//                    REQUEST_COURSE_ACCESS);
//            return;
//        } else {
//            permissionGranted = true;
//        }
//        if(permissionGranted) {
//            mLocationManager.removeUpdates( mLocationListener);
//        }
        checkText();
    }


//        randomText();
//        mSummaryTextView.setText(randomText().toString());
//        Toast.makeText(this, mSummaryTextView.getText(), Toast.LENGTH_SHORT).show();

//        mSwitchCamera = (Button) findViewById(R.id.switch_camera);
//        mSwitchCamera.setOnClickListener(mSwitchCameraButtonClickListener);

//        mBackImageButton = (ImageButton)findViewById(R.id.backImageButton);
//        mBackImageButton.setOnClickListener(mBackButtonClickListener);


    private OnClickListener mBackButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(BMSummary.this, CameraActivity.class);
            BMSummary.this.startActivity(intent);
//            Toast.makeText(getBaseContext(), "Testing", Toast.LENGTH_SHORT).show();
//            finish();

        }
    };

    public void checkText() {
//        if (mImageMain == R.mipmap.fart_shart) {
//            mSummary.setText("Eat shit");
//        }

        switch (mImageMain) {
            case (R.mipmap.gut):
                mSummary.setText("BM Summary");
                break;
            case(R.mipmap.fart_shart):
                mSummary.setText("Shit or fffffffffftttt");
                break;
        }
    }


    public String randomText() {

        String[] array = this.getResources().getStringArray(R.array.bottomline);
        String randomHeading = array[new Random().nextInt(array.length)];

        return randomHeading;
    }

    private String randomCountdown() {
        String[] array = this.getResources().getStringArray(R.array.countdown);
        String countdown = array[new Random().nextInt(array.length)];
        return countdown;
    }

    private String randomExpiry() {
        String[] array = this.getResources().getStringArray(R.array.expiry);
        String expiry = array[new Random().nextInt(array.length)];
        return expiry;
    }

    private String randomText(String[] stringArray) {
        String[] array = stringArray;
        String string = array[new Random().nextInt(array.length)];
        return string;
    }

    private OnClickListener showToast = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getBaseContext(), "Testing", Toast.LENGTH_SHORT).show();
//            Intent myIntent = new Intent(BMSummary.class, MainActivity.class);
//            BMSummary.this.startActivity(myIntent);
//            finish();
//            setContentView(R.layout.activity_main);

        }
    };

    private OnClickListener mapButtonClickListener = (new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent mapIntent = new Intent(BMSummary.this, Locations.class);
            BMSummary.this.startActivity(mapIntent);
        }
    });

}
