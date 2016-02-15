package com.example.iain.imagepixel;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Iain on 1/29/2016.
 */
public class DeviceList extends AppCompatActivity {

        Button btnPaired, btnSwitch;
        ListView devicelist;

        private BluetoothAdapter myBluetooth = null;
        private Set<BluetoothDevice> pairedDevices;
        public static String EXTRA_ADDRESS = "device_address";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_device_list);

                btnPaired = (Button)findViewById(R.id.button);
                btnSwitch = (Button) findViewById(R.id.toMain);
                devicelist = (ListView)findViewById(R.id.listView);

                myBluetooth = BluetoothAdapter.getDefaultAdapter();

                if(myBluetooth == null) {
                        Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
                        finish();
                } else if(!myBluetooth.isEnabled()) {
                        Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(turnBTon,1);
                }

                btnPaired.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                pairedDevicesList();
                        }
                });

                btnSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(DeviceList.this, MainActivity.class));
                    }
                });
        }

        private void pairedDevicesList() {
                pairedDevices = myBluetooth.getBondedDevices();
                ArrayList list = new ArrayList();

                if(pairedDevices.size()>0) {
                        for(BluetoothDevice bt : pairedDevices) {
                                list.add(bt.getName() + "\n" + bt.getAddress());
                        }
                } else {
                        Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
                }

                final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
                devicelist.setAdapter(adapter);
                devicelist.setOnItemClickListener(myListClickListener);
        }
    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            Intent i = new Intent(DeviceList.this, MainActivity.class);

            i.putExtra(EXTRA_ADDRESS, address);
            startActivity(i);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_device_list, menu);
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
}
