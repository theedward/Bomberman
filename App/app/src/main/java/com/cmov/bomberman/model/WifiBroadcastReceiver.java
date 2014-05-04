package com.cmov.bomberman.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

import com.cmov.bomberman.controller.WifiActivity;

import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;

public class WifiBroadcastReceiver extends BroadcastReceiver{

    WifiP2pManager manager;
    Channel channel;
    WifiP2pDeviceList wifiP2pDeviceList;
    WifiActivity wifiActivity;

    public WifiBroadcastReceiver(WifiP2pManager manager, Channel channel, WifiActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.wifiActivity = activity;
    }

    public List<String> getGroupOwnersList(WifiP2pDeviceList devices) {
        List<String> groupOwners = new LinkedList<String>();

        if (devices.getDeviceList() != null) {
            for (WifiP2pDevice device : devices.getDeviceList()) {
                //if (device.isGroupOwner()) {
                groupOwners.add(device.deviceName);
                //}
            }
        }
        return groupOwners;
    }

    public WifiP2pDeviceList getWifiP2pDeviceList() {
        return wifiP2pDeviceList;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi P2P is enabled
            } else {
                // Wi-Fi P2P is not enabled
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (manager != null) {
                manager.requestPeers(channel, new PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
                        WifiBroadcastReceiver.this.wifiP2pDeviceList = wifiP2pDeviceList;
                        Log.i("WifiBroadcastReceiver:", "PEERS CHANGED: NUMBER OF PEERS :" + wifiP2pDeviceList.getDeviceList().size());
                        wifiActivity.onUpdateDevice(getGroupOwnersList(wifiP2pDeviceList));
                    }
                });

            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections

            if (manager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {

                // We are connected with the other device, request connection
                // info to find group owner IP
                manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                        // InetAddress from WifiP2pInfo struct.
                        String groupOwnerAddress = info.groupOwnerAddress.getHostAddress();

                        // After the group negotiation, we can determine the group owner.
                        if (info.groupFormed && info.isGroupOwner) {
                            // Do whatever tasks are specific to the group owner.
                            // One common case is creating a server thread and accepting
                            // incoming connections.
                        } else if (info.groupFormed) {
                            // The other device acts as the client. In this case,
                            // you'll want to create a client thread that connects to the group
                            // owner.
                        }
                    }
                });
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }
}
