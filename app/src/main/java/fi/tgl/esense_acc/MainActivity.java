package fi.tgl.esense_acc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.esense.esenselib.*;

public class MainActivity extends Activity implements ESenseConnectionListener, ESenseSensorListener {

    private static final String TAG = "MainActivity";
    private static final String DeviceName = "eSense-0056";
    private boolean isMeasuring;
    private boolean isFirst;
    TextView statusText;
    Button startButton;
    private ArrayList<Long> timeData;
    private ArrayList<ArrayList<Short>> data;
    private long time;
    private long timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isMeasuring = false;
        isFirst = false;
        timestamp = 0l;
        time = 0l;
        statusText = findViewById(R.id.Status);
        startButton = findViewById(R.id.StartButton);
        statusText.setText("Finding eSense...");

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
        Log.d(TAG, "onSensorChanged: " + val[0] + ", " + val[1] + ", " + time);

        for (int i = 0; i < 3; i++)
        {
            data.get(i).add(val[i]);
        }
        if (isFirst)
        {
            time = eSenseEvent.getTimestamp();
            //eSenseEvent.setTimestamp(0l);
            isFirst = false;
        }
        timeData.add(eSenseEvent.getTimestamp() - time);
    }

    public void onClickButton(View v) {
        Log.d(TAG, "onClick");
        switch (v.getId()) {
            case R.id.StartButton:
                if (isMeasuring){
                    isMeasuring = false;
                    startButton.setText(R.string.start_button_text);
                    statusText.setText("Tap to Start");
                    OutputFile();
                }
                else {
                    isMeasuring = true;
                    isFirst = true;
                    startButton.setText(R.string.stop_button_text);
                    statusText.setText("Measuring...");
                    data = new ArrayList<>();
                    timeData = new ArrayList<>();
                    for (int i = 0; i < 3; i++)
                    {
                        ArrayList<Short> arr = new ArrayList<>();
                        data.add(arr);
                    }

                }
                break;
        }
    }

    private void OutputFile() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_kkmmss");
        String filename = sdf.format(date) + ".csv";
        Log.d(TAG, filename);
        try {
            FileOutputStream fout = openFileOutput(filename, MODE_PRIVATE);
            String comma = ",";
            String newline = "\n";
            for (int i = 0; i < data.get(0).size(); i++) {
                for (int j = 0; j < 3; j++)
                {
                    fout.write(String.valueOf(data.get(j).get(i)).getBytes());
                    fout.write(comma.getBytes());
                }
                fout.write(String.valueOf(timeData.get(i)).getBytes());
                fout.write(newline.getBytes());
            }
            fout.close();
            Log.d(TAG, "File created.");
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Cannot open file.");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "Cannot write string.");
            e.printStackTrace();
        }
    }
}
