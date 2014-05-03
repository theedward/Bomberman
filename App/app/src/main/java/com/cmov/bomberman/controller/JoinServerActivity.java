package com.cmov.bomberman.controller;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;

import com.cmov.bomberman.R;

import java.util.LinkedList;
import java.util.List;

public class JoinServerActivity extends WifiActivity {

    private final String TAG = this.getClass().getSimpleName();

    public List<WifiP2pDevice> getGroupOwnersList() {
        List<WifiP2pDevice> groupOwners = new LinkedList<WifiP2pDevice>();

        for (WifiP2pDevice device : mReceiver.getWifiP2pDeviceList().getDeviceList()) {
            if (device.isGroupOwner()) {
                groupOwners.add(device);
            }
        }
        return groupOwners;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_server);

        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "discovered Peers");
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.i(TAG, "could not discover Peers");
            }
        });


        List<WifiP2pDevice> groupOwners = getGroupOwnersList();

        // must show the list to the user that wants to join the server
        // must show groupOwners

        // when the user chooses a server to join must assign the server device
        // to the device
        WifiP2pDevice serverDevice;

        WifiP2pConfig config = new WifiP2pConfig();
        //config.deviceAddress = serverDevice.deviceAddress;

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                //success logic
            }

            @Override
            public void onFailure(int reason) {
                //failure logic
            }
        });
    }


}
