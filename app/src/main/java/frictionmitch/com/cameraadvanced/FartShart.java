package frictionmitch.com.cameraadvanced;


import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Chronometer;

public class FartShart extends Activity {

    private Chronometer mChronometer;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fart);

        mChronometer = (Chronometer)findViewById(R.id.chronometer);
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
    }

    public void setCoundDown(boolean countDown) {

    }
}
