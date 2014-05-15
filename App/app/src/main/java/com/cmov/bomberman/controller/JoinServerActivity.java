package com.cmov.bomberman.controller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.cmov.bomberman.R;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pDevice;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pDeviceList;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pInfo;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pManager;

import java.util.ArrayList;
import java.util.Map;

public class JoinServerActivity extends Activity implements OnWifiP2pState {
    private final String TAG = getClass().getSimpleName();

	private String username;
	private ArrayAdapter<String> serverAdapter;

	private SimWifiP2pDevice groupOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_server);

        Typeface blockFonts = Typeface.createFromAsset(getAssets(), "Inconsolata-Regular.ttf");
        TextView bombermanTxt = (TextView) findViewById(R.id.bombermantxt);
        TextView choseServer = (TextView) findViewById(R.id.choseServers);
        final Button joinServer = (Button) findViewById(R.id.joinServer);
        bombermanTxt.setTypeface(blockFonts);
        choseServer.setTypeface(blockFonts);
        joinServer.setTypeface(blockFonts);

		joinServer.setEnabled(false);

		serverAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());

        ListView serversList = (ListView) findViewById(R.id.serversList);
		serversList.setAdapter(serverAdapter);

		// Get username from intent
        Bundle extras = getIntent().getExtras();
		if (extras != null) {
			username = extras.getString("username");
		} else {
			Log.e(TAG, "Bundle is null");
		}

		P2pApplication.getInstance().setOnWifiP2pState(this);
    }

    public void joinServer(final View view) {
        if (groupOwner != null) {
			Toast.makeText(this, "JOINING GAME CUARALHO", Toast.LENGTH_SHORT).show();

			// jump to the game activity
            Intent intent = new Intent(JoinServerActivity.this, MultiPlayerGameActivity.class);
            intent.putExtra("isServer", false);
            intent.putExtra("username", username);
			intent.putExtra("hostname", groupOwner.getVirtIp());
            startActivity(intent);
        }
    }

	@Override
	public void onWifiOn() {
		// Nothing to do here
		Toast.makeText(this, "WiFi Direct enabled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onWifiOff() {
		Toast.makeText(this, "WiFi Direct disabled", Toast.LENGTH_SHORT).show();

		final Button joinServer = (Button) findViewById(R.id.joinServer);
		joinServer.setEnabled(false);
	}

	@Override
	public void onPeersChanged() {
		// Nothing to do here
		Toast.makeText(this, "Peer list changed", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onNetworkMembershipChanged(final SimWifiP2pInfo info) {
		Toast.makeText(this, "Network membership changed", Toast.LENGTH_SHORT).show();

		serverAdapter.clear();

		if (info.askIsClient()) {
			// Joined a network. Request the group info to obtain the server address
			P2pApplication.getInstance().requestInGroup(new SimWifiP2pManager.GroupInfoListener() {
				@Override
				public void onGroupInfoAvailable(final SimWifiP2pDeviceList devices, final SimWifiP2pInfo groupInfo) {
					Map<String, ArrayList<String>> groups = groupInfo.getExistingGroups();
					for (SimWifiP2pDevice device : devices.getDeviceList()) {
						// if the device is the group owner and it's connectable, it's a valid choice
						if (groups.containsKey(device.deviceName) && groupInfo.askIsConnectionPossible(device.deviceName)) {
							// Only show the devices in the network that are not me
							if (!device.deviceName.equals(info.getDeviceName())) {
								serverAdapter.add(device.deviceName);
							}

							// this is a valid device
							groupOwner = device;

							// Now it can proceed join the game.
							final Button joinServer = (Button) findViewById(R.id.joinServer);
							joinServer.setEnabled(true);
						}
					}
				}
			});
		}
	}

	@Override
	public void onGroupOwnershipChanged(final SimWifiP2pInfo info) {
		// Nothing to do here
		Toast.makeText(this, "Group ownership changed", Toast.LENGTH_SHORT).show();
	}
}
