package zar.ase.eu.zarsimulator;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {


    float gravity[] = new float[3];
    float linear_acceleration[] = new float[3];

    long ShakeTimestamp;
    private final int SHAKE_STOP_TIME_MS = 500;
    private final int SHAKE_COUNT_RESET_TIME_MS = 3000;

    SensorManager sensorMgr;
    Sensor sensor;
    TextView zar1;
    TextView zar2;

    int THRESHOLD = 5;
    int SHAKE_COUNT = 0;

    float lastValues[] = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        zar1 = findViewById(R.id.zar1);
        zar2 = findViewById(R.id.zar2);

        sensorMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorMgr.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorMgr.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.8f;

        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Isolate the force of gravity with the low-pass filter.
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];

        if ((linear_acceleration[0] > THRESHOLD || linear_acceleration[0] < -THRESHOLD) ||
                (linear_acceleration[1] > THRESHOLD || linear_acceleration[1] < -THRESHOLD) ||
                (linear_acceleration[2] > THRESHOLD || linear_acceleration[2] < -THRESHOLD)) {

            long now = System.currentTimeMillis();
            if (ShakeTimestamp + SHAKE_STOP_TIME_MS > now) {
                return;
            }
            if (ShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                SHAKE_COUNT = 0;
            }
            ShakeTimestamp = now;

            if (SHAKE_COUNT > 3) {
                SHAKE_COUNT = 0;
            } else {
                SHAKE_COUNT++;
            }
            lastValues[0] = linear_acceleration[0];
            lastValues[1] = linear_acceleration[1];
            lastValues[2] = linear_acceleration[2];

            if (lastValues[0] == linear_acceleration[0] && lastValues[1] == linear_acceleration[1] && lastValues[2] == linear_acceleration[2]) {
                zar1.setText(String.valueOf(new Random().nextInt(5) + 1));
                zar2.setText(String.valueOf(new Random().nextInt(5) + 1));
                vib.vibrate(100);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
