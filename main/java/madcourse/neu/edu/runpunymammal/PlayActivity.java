package madcourse.neu.edu.runpunymammal;

import android.app.Activity;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.lang.Math.*;

/**
 * Created by douglas on 3/22/17.
 */

public class PlayActivity extends Activity {
    SensorManager mySensorManager;
    Sensor myStepDetector;
    Sensor myAccelerometer;
    SensorEventListener mySensorListener;
    int steps = 0;
    int upper_threshold = 25;
    int lower_threshold = 0;


    int jog_run_threshold = 20;
    int walk_jog_threshold = 15;
    int stand_walk_threshold = 10;
    int are_you_in_a_car = 100;
/*these may be a little low, to account for the fact that the intended audience is children.
 */

    //40 is threshold between jogging and running
    //25 is bound between walking and jogging
    //anything below 25 may be indistinguishable from walking

    //TextView stepView;
   // int test = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_main);
        //stepView = (TextView) findViewById(R.id.stepTest);

        final SharedPreferences sp = getSharedPreferences(getResources().getString(R.string.OURINFO), MODE_PRIVATE);
        final SharedPreferences.Editor ed = sp.edit();
        ed.putInt(getResources().getString(R.string.steps), steps);
        ed.commit();
        setSpeed(getResources().getString(R.string.jog));

        mySensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        myStepDetector = mySensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        myAccelerometer = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //change to accelerometer
        /*
        if (myAccelerometer != null) {
            steps = 7; //for testing
            ed.putInt("steps", steps);
            ed.commit();
        }*/


        mySensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                double accel = Math.sqrt(x*x + y*y + z*z);

                if (accel >= lower_threshold && accel <= upper_threshold) {

                    steps++;
                    ed.putInt(getResources().getString(R.string.steps), steps);
                    ed.commit();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        mySensorManager.registerListener(mySensorListener, myAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        //mySensorManager.registerListener(mySensorListener, myStepDetector, SensorManager.SENSOR_DELAY_GAME);

    }

    public void setSpeed(String speed) {
        if (speed.equals(getResources().getString(R.string.jog))) {
            upper_threshold = jog_run_threshold;
            lower_threshold = walk_jog_threshold;
        } else if (speed.equals(getResources().getString(R.string.run))){
            upper_threshold = are_you_in_a_car;
            lower_threshold = jog_run_threshold;
        } else if (speed.equals(getResources().getString(R.string.walk))) {
            upper_threshold = walk_jog_threshold;
            lower_threshold = stand_walk_threshold;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mySensorManager.registerListener(mySensorListener, myAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try{
            mySensorManager.unregisterListener(mySensorListener);
        } catch (Exception e) {}    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            mySensorManager.unregisterListener(mySensorListener);
        } catch (Exception e) {}
    }
}
