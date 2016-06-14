package com.example.iot;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private static final Map<String, List<String>> PLACES_BY_BEACONS;
    private String workState = " ";
    public static String kitchenMsg = "kitchen";
    public static String workMsg = "work";
    public static String outOfBuildingMsg = "outofbuilding";
    private static String dweetKey;

    static {
        Map<String, List<String>> placesByBeacons = new HashMap<>();
        placesByBeacons.put("25833:63972", new ArrayList<String>() {{
            add(kitchenMsg);
            add(workMsg);
            add(outOfBuildingMsg);
        }});
        placesByBeacons.put("4496:14972", new ArrayList<String>() {{
            add(workMsg);
            add(kitchenMsg);
            add(outOfBuildingMsg);
        }});
        placesByBeacons.put("54001:8231", new ArrayList<String>() {{
            add(outOfBuildingMsg);
            add(kitchenMsg);
            add(workMsg);
        }});
        PLACES_BY_BEACONS = Collections.unmodifiableMap(placesByBeacons);
    }

    private List<String> placesNearBeacon(Beacon beacon) {
        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());
        if (PLACES_BY_BEACONS.containsKey(beaconKey)) {
            return PLACES_BY_BEACONS.get(beaconKey);
        }
        return Collections.emptyList();
    }

    private BeaconManager beaconManager;
    private Region region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Properties props=new Properties();
        AssetManager assetManager = getBaseContext().getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open("dweet.properties");
            props.load(inputStream);
            dweetKey = props.getProperty("key");
        } catch (IOException e) {
            e.printStackTrace();
        }

        beaconManager = new BeaconManager(this);
        final EditText idText = (EditText) findViewById(R.id.workerId);
        Button submit = (Button) findViewById(R.id.button);
        submit.setText("Submit ID");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MyApplication) getApplicationContext()).setId(Integer.parseInt(idText.getText().toString()));
            }
        });
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty() && ((MyApplication) getApplicationContext()).getId() != -1) {
                    Beacon nearestBeacon = list.get(0);
                    List<String> places = placesNearBeacon(nearestBeacon);
                    if (!places.isEmpty()) {
                        if (!places.get(0).equals(workState)) {
                            showNotification("Stan pracy", places.get(0), workState);
                        }
                    }
                }
            }
        });
        region = new Region("ranged region", null, null, null);
    }

    private void get(String state, String previousState) {
        AsyncHttpClient client = new AsyncHttpClient();
        int diffInSeconds = (int) ((new Date().getTime() - ((MyApplication) getApplicationContext()).getLastCheckout().getTime())
                / (1000));
        final DeviceData deviceData = new DeviceData(((MyApplication) getApplicationContext()).getId(), previousState, state, diffInSeconds);
        client.get("https://dweet.io/dweet/for/job_tracker_1?key=" +
                        dweetKey +
                        "&id=" + deviceData.getDeviceId() +
                        "&locationbefore=" + deviceData.getLocationBefore() +
                        "&locationafter=" + deviceData.getLocationAfter() +
                        "&time_spent_kitchen=" + deviceData.getTimeSpentKitchen() +
                        "&time_spent_outside=" + deviceData.getTimeSpentOutside() +
                        "&time_spent_work=" + deviceData.getTimeSpentWork() +
                        "&kitchen=" + deviceData.getKitchen() +
                        "&left=" + deviceData.getLeft() +
                        "&work=" + deviceData.getWork(),
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        ((MyApplication) getApplicationContext()).setLastCheckout(new Date());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showNotification(String title, String state, String previousState) {
        workState = state;
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[]{notifyIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(workState)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
        get(state, previousState);
    }
}
