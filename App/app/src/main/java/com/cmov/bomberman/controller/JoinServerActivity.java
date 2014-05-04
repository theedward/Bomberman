package com.cmov.bomberman.controller;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.cmov.bomberman.R;

import java.util.LinkedList;
import java.util.List;

public class JoinServerActivity extends WifiActivity {


    private String username;
    ListView listView;
    private final String TAG = this.getClass().getSimpleName();
    WifiP2pDevice ownerDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_server);


        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() { Log.i(TAG, "discovered Peers"); }

            @Override
            public void onFailure(int reasonCode) {
                Log.i(TAG, "could not discover Peers");
            }
        });


    }

    @Override
    public void onUpdateDevice(final List<String> groupOwners) {

        /**************************** OWNERS LIST *************************************************/

        listView = (ListView) findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(JoinServerActivity.this,
                android.R.layout.simple_list_item_1, android.R.id.text1, groupOwners);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // ListView Clicked item index
                int itemPosition = position;
                // ListView Clicked item value
                String itemValue = (String) listView.getItemAtPosition(position);
                ownerDevice = getOwnerDeviceByName(groupOwners, position);
                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
                        .show();
            }
        });

        WifiP2pConfig config = new WifiP2pConfig();
        if (ownerDevice != null) {
            config.deviceAddress = ownerDevice.deviceAddress;

            mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.i(TAG, "successfully connected to group owner");
                }

                @Override
                public void onFailure(int reason) {
                    Log.i(TAG, "could not connect to group owner");
                }
            });
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    public void accept(final View view){

        /**************************** USERNAME ****************************************************/
        EditText usernameBox = (EditText)findViewById(R.id.chose_username_editable_join);
        username = usernameBox.getText().toString();

        // Go to the LoadingGameActivity
        final Intent intent = new Intent(this, LoadingActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("isOwner", false);
        startActivity(intent);
    }

    public WifiP2pDevice getOwnerDeviceByName(List<String> owners, int position) {
        WifiP2pDevice owner= null;

        final String deviceName = owners.get(position);
        for (WifiP2pDevice device : mReceiver.getWifiP2pDeviceList().getDeviceList()) {
            if (device.deviceName.equals(deviceName)) {
                owner = device;
            }
        }
        return  owner;
    }
}
