package fi.tgl.esense_acc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import io.esense.esenselib.*;

public class MainActivity extends Activity implements ESenseConnectionListener, ESenseSensorListener {

    private static final String TAG = "MainActivity";
    private static final String DeviceName = "eSense-0056";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ESenseManager manager = new ESenseManager(DeviceName, this,this);
        manager.connect(2000);
    }

    @Override
    public void onDeviceFound(ESenseManager eSenseManager) {
        Log.d(TAG, "onDeviceFound");
    }

    @Override
    public void onDeviceNotFound(ESenseManager eSenseManager) {
        Log.d(TAG, "onDeviceNotFound");
    }

    @Override
    public void onConnected(ESenseManager eSenseManager) {
        Log.d(TAG, "onConnected");
        eSenseManager.registerSensorListener(this,100);
    }

    @Override
    public void onDisconnected(ESenseManager eSenseManager) {
        Log.d(TAG, "onDisconnected");
    }

    @Override
    public void onSensorChanged(ESenseEvent eSenseEvent) {
        //Log.d(TAG, "onSensorChanged");
        short[] val = eSenseEvent.getAccel();
        Log.d(TAG, "onSensorChanged: " + val[0] + ", " + val[1] + ", " + val[2]);
    }
}
