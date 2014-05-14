package com.cmov.bomberman.controller;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.*;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;

import java.net.InetAddress;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ApplicationP2PInfo extends Application {

    static Context context;
    static public WifiP2pManager mManager;
    static public Channel mChannel;
    static public SimWifiP2pBroadcastReceiver mReceiver;
    static public IntentFilter mIntentFilter;
    static public WifiP2pDevice groupOwnerDevice;

    static String groupOwner;
    static InetAddress groupOwnerAddress;
    static Collection<WifiP2pDevice> groupClients;
    static List<String> groupClientsNames;

    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();
        mManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = ApplicationP2PInfo.mManager.initialize(this, getMainLooper(), null);
//        mReceiver = new SimWifiP2pBroadcastReceiver(mManager, mChannel);

        Log.i("AppInfo", "created app info , manager ,channel etc...");
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    /*
    * Creates a peer to peer group with the current device as owner
    * It this operation is successful requests group information and
    * */
    static public void createP2PGroup(final String tag) {

        mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.i(tag, "Created p2p group");
                requestP2PGroupInfo();
            }

            @Override
            public void onFailure(int reason) {
                Log.i(tag, "Could not create p2p group");
            }
        });
    }


    static public void requestP2PGroupInfo() {
        mManager.requestGroupInfo(ApplicationP2PInfo.mChannel, new WifiP2pManager.GroupInfoListener() {
            @Override
            public void onGroupInfoAvailable(WifiP2pGroup wifiP2pGroup) {
                groupOwnerDevice = wifiP2pGroup.getOwner();
                groupOwner = groupOwnerDevice.deviceName;
                Log.i("app info", "owner device name" + groupOwner);
                groupClients = wifiP2pGroup.getClientList();
                groupClientsNames = listDeviceToListName(groupClients);
            }
        });
    }



    static public void connectToP2POwner(final String tag) {

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = groupOwnerAddress.getHostAddress();
        config.wps.setup = WpsInfo.PBC;

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.i(tag, "Successfully connected to P2P group");
            }

            @Override
            public void onFailure(int i) {
                Log.i(tag, "Could not connect to P2P group");
            }
        });
    }

    static public void requestP2PConnectionInfo() {
        mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;
            }
        });
    }

    static public void discoverPeers(final String tag) {

        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.i(tag, "successfully discovered peers");
            }

            @Override
            public void onFailure(int i) {
                Log.i(tag, "Could not discover peers");
            }
        });
    }

    static public List<String> listDeviceToListName(Collection<WifiP2pDevice> deviceList) {

        List<String> list = new LinkedList<String>();

        if (! deviceList.isEmpty()) {
            for (WifiP2pDevice device : deviceList) {
                list.add(device.deviceName);
            }
        }
        return list;
    }
}
