package com.example.opopkk.embedded;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BeaconConsumer{
    static int is_used = 0;
    ArrayList<String> al = new ArrayList<>();

    private BeaconManager beaconManager;
    private List<Beacon> beaconList = new ArrayList<>();

    long start_over_1m;
    int is_start_over_1m = 0;
    int count = 0;

    void make_list_text(){
        int max_capacity = 10;
        for(int i = 0; i < max_capacity; i++){
            al.add(String.valueOf(i + 1) + "번자리");
        }
    }
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        make_list_text();

        lv = (ListView) findViewById(R.id.main_list);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, al);
        lv.setAdapter((adapter));
        lv.setOnItemClickListener(new click_cell());

        beaconManager = BeaconManager.getInstanceForApplication(this);

        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);

//        Log.e("asdasd", "Asdasdasdasd");
        handler.sendEmptyMessage(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if(beacons.size() > 0){
                    beaconList.clear();
                    for (Beacon beacon: beacons){
                        beaconList.add(beacon);
                    }
                }
            }
        });

        try{
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null,null,null));
        }catch (RemoteException e) {}
    }

    class click_cell implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            //Toast.makeText(getApplicationContext(), al.get(i), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, UserId.class);
            intent.putExtra("index", i + 1);
            startActivityForResult(intent, 0);
        }
    }
    void over_1m(){
        if(count==0){
            Toast.makeText(getApplicationContext(), "1m가 넘었습니다.", Toast.LENGTH_SHORT).show();
            count = 1;
        }
        if(is_start_over_1m == 1){
            if (System.currentTimeMillis() - start_over_1m > 100000){
//                Log.e("didi", "3ch sjadjTek");
//                Toast.makeText(getApplicationContext(), "3초 넘었다", Toast.LENGTH_SHORT).show();
                changed_view = lv.getChildAt(changed_index - 1);
                changed_view.setBackgroundColor(Color.parseColor("#ffffff"));
                Toast.makeText(getApplicationContext(), "자동 퇴실 되었습니다.", Toast.LENGTH_SHORT).show();
                TextView t1 = changed_view.findViewById(android.R.id.text1);
                t1.setText(String.valueOf(changed_index) + "번자리");
                is_used = 0;
            }
        }
    }

    Handler handler = new Handler (){
        public void handleMessage(Message msg){
            if(is_used == 1) {
                Log.e("asdasd", "Asdasda`12sdasd" + beaconList.size());
                for (Beacon beacon : beaconList) {
                    if(Double.parseDouble(String.format("%.3f", beacon.getDistance())) > 1.0){
                        if(is_start_over_1m == 0)  {
                            is_start_over_1m = 1;
                            start_over_1m = System.currentTimeMillis();
                            Log.e("didi", "start over 1m");
                        }
                        over_1m();
                    }else{
                        if(is_start_over_1m == 1)  {
                            is_start_over_1m = 0;
                            Toast.makeText(getApplicationContext(), "reset!", Toast.LENGTH_SHORT).show();
                            count=0;
                        }
                    }
                    String a = "ID: " + beacon.getId2() + " / " + "Distance : " + Double.parseDouble(String.format("%.3f", beacon.getDistance())) + "m\n";
                    Log.e("didididis", a);
                }
            }
            handler.sendEmptyMessageDelayed(0, 500);
        }
    };
    View changed_view;
    int changed_index;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1){
            int id = data.getIntExtra("id", -1);
//            Toast.makeText(getApplicationContext(), String.valueOf(id), Toast.LENGTH_SHORT).show();
            int index = data.getIntExtra("index", -1);

            if(id != -1) {
                changed_view = lv.getChildAt(index - 1);
                changed_view.setBackgroundColor(Color.parseColor("#ff0000"));
                TextView t1 = changed_view.findViewById(android.R.id.text1);
                t1.setText(String.valueOf(index) + "번자리      학번:" + String.valueOf(id));
                is_used = 1;
                changed_index = index;
            }
            lv.invalidate();
        }
    }
}
