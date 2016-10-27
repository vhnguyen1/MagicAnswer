package edu.orangecoastcollege.cs273.magicanswer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Takes in device movements, analyzing and determining whether or not
 * the movements are appropriate shakes for the MagicAnswer.
 *
 * @author Vincent Nguyen
 */
public class ShakeDetector implements SensorEventListener {

    // Constant to represent the shake threshold
    // You may adjust the shakeThreshold to whatever you want, but 25.0f is a good value
    // that constitutes a shake
    private static final float SHAKE_THRESHOLD = 25.0f;
    // Constant to represent how long between each shakes (in milliseconds)
    // This case is 2 seconds (2000 milliseconds)
    private static final int SHAKE_TIME_LAPSE = 2000;
    // Stores when was the last time the event occurred
    // Long since the number can be very large (since milliseconds)
    private long timeOfLastShake = 0;
    // Define a Listener to register onShake events
    private OnShakeListener shakeListener;
    // Make a constructor to create a new ShakeDetector, passing in a new OnShakeListener as an
    // argument
    public ShakeDetector(OnShakeListener listener) {
        shakeListener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // Determine if the event is an Accelerometer
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Get the x, y, and z values when this event occurs
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            // Use the distance formula to measure the rate of acceleration and g-force
            // of the movements
            // Distance formula -> d = sqrt((x2 - x1)^2 + (y2 - y1)^2 + (z2 - z1)^2)
            // In our case -> d = sqrt((x - gravity)^2 + (y - gravity)^2 + (z - gravity)^2)
            // Compare all 3 values against gravity
            float gForceX = x - SensorManager.GRAVITY_EARTH;
            float gForceY = y - SensorManager.GRAVITY_EARTH;
            float gForceZ = z - SensorManager.GRAVITY_EARTH;

            // Compute the sum of all the squares
            double vector = Math.pow(gForceX, 2.0) + Math.pow(gForceY, 2.0)
                    + Math.pow(gForceZ, 2.0);

            // Compute the gForce
            float gForce = (float) Math.sqrt(vector);

            // Compare gForce with the threshold
            // If the gForce is greater than the threshold, then the shake is recognized
            if (gForce > SHAKE_THRESHOLD) {
                // Get the current time
                long now = System.currentTimeMillis();
                // Compare to see if the current is at least 2000 milliseconds greater than the
                // time of the last/previous shake
                if (now - timeOfLastShake >= SHAKE_TIME_LAPSE) {
                    timeOfLastShake = now;
                    // Register a shake event!!!
                    shakeListener.onShake();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    // Define our own interface (method for other classes to implement called onShake()
    // The code for it is going to be handled in the activity (MagicAnswerActivity.java)
    // It's the responsibility of the activity to implement this method
    public interface OnShakeListener {
        void onShake();
    }
}