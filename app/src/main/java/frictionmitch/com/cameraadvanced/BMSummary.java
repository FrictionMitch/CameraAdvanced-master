package frictionmitch.com.cameraadvanced;


import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
    private TextView mTextView;
    private TextView mRandomTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        mBackButton = (Button) findViewById(R.id.back_button);
        mBackButton.setOnClickListener(mBackButtonClickListener);



        mTextView = (TextView)findViewById(R.id.textViewSummary);
        mTextView.setText(randomText());

        mRandomTextView = (TextView)findViewById(R.id.randomTextView);
        mRandomTextView.setText(randomTime());
//        randomText();
//        mTextView.setText(randomText().toString());
//        Toast.makeText(this, mTextView.getText(), Toast.LENGTH_SHORT).show();

//        mSwitchCamera = (Button) findViewById(R.id.switch_camera);
//        mSwitchCamera.setOnClickListener(mSwitchCameraButtonClickListener);

//        mBackImageButton = (ImageButton)findViewById(R.id.backImageButton);
//        mBackImageButton.setOnClickListener(mBackButtonClickListener);
    }

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

        String[] array = this.getResources().getStringArray(R.array.heading);
        String randomHeading = array[new Random().nextInt(array.length)];

        return randomHeading;
    }

    private String randomTime() {
        String[] array = this.getResources().getStringArray(R.array.no_time);
        String randomTime = array[new Random().nextInt(array.length)];
        return randomTime;
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

}
