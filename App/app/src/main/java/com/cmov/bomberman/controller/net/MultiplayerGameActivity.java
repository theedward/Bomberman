package com.cmov.bomberman.controller.net;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;

import com.cmov.bomberman.R;
import com.cmov.bomberman.model.net.WifiBroadcastReceiver;

import java.util.List;


public class MultiplayerGameActivity extends Activity {
    public static final String TAG = "MultiplayerGameActivity";
    private boolean isServer;

    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;

    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private BroadcastReceiver receiver = null;

    /**********************************************************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        final Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            String username = extras.getString("username");
            isServer = extras.getBoolean("isServer");
        }
        if (isServer) {
            setContentView(R.layout.fragment_server_waiting);
        } else {
            setContentView(R.layout.fragment_device_list);
        }
    }

    /**********************************************************************************************/
    /** register the BroadcastReceiver with the intent values to be matched */
    @Override
    public void onResume() {
        super.onResume();
        receiver = new WifiBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    /**********************************************************************************************/
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
    /**********************************************************************************************/

    public void instantiateGame(int level, List<String> players, String host, String port) {

    }
}
