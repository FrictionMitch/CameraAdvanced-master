package frictionmitch.com.cameraadvanced;


import java.util.Random;

import android.*;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class BMSummary extends Activity {

//    private static final int TAKE_PICTURE_REQUEST_B = 100;

    private Button mBackButton;
    private ImageButton mBackImageButton;
    private TextView mSummaryTextView;
    private TextView mRandomTextView;
    private TextView mCountdownTextView;
    private TextView mExpiryTextView;
    private TextView mGirthTextView;
    private TextView mWeightTextView;
    private TextView mFlushesTextView;
    private TextView mViscosityTextView;
    private Button mMapButton;

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

        mWeightTextView = (TextView)findViewById(R.id.weightTextView);
        mWeightTextView.setText(randomText(this.getResources().getStringArray(R.array.weight)));

        mFlushesTextView = (TextView)findViewById(R.id.flushesTextView);
        mFlushesTextView.setText(randomText(this.getResources().getStringArray(R.array.flushes)));

        mViscosityTextView = (TextView)findViewById(R.id.viscosityTextView);
        mViscosityTextView.setText(randomText(this.getResources().getStringArray(R.array.viscosity)));



        mMapButton = (Button)findViewById(R.id.mapButton);
        mMapButton.setOnClickListener(mapButtonClickListener);

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
