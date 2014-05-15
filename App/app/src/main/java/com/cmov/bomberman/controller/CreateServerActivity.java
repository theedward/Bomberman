package com.cmov.bomberman.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.cmov.bomberman.R;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pDeviceList;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pInfo;
import pt.utl.ist.cmov.wifidirect.SimWifiP2pManager;

import java.util.ArrayList;

public class CreateServerActivity extends Activity implements OnWifiP2pState {
    private final String TAG = getClass().getSimpleName();
	private final int LEVEL_DEFAULT = 1;
	// TODO support more levels

	private int level = LEVEL_DEFAULT;
	private String username;
	private SimWifiP2pDeviceList deviceList;

	private Button startButton;
	private ArrayAdapter<String> devicesAdapter;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_server);

		devicesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());

		startButton = (Button) findViewById(R.id.startgamebtn);
		startButton.setEnabled(false);

		ListView playersList = (ListView) findViewById(R.id.playersList);
		playersList.setAdapter(devicesAdapter);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			username = extras.getString("username");
		} else {
			Log.e(TAG, "Bundle is null");
		}

		P2pApplication.getInstance().setOnWifiP2pState(this);
	}

	public void startGame(final View view) {
		Toast.makeText(this, "STARTING GAME CUARALHO", Toast.LENGTH_SHORT).show();

		// jump to the game activity
		Intent intent = new Intent(this, MultiPlayerGameActivity.class);
		intent.putExtra("isServer", true);
		intent.putExtra("username", username);
		intent.putExtra("level", level);
		startActivity(intent);
	}

	@Override
	public void onWifiOn() {
		// Empty on purpose
		Toast.makeText(this, "WiFi Direct enabled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onWifiOff() {
		Toast.makeText(this, "WiFi Direct disabled", Toast.LENGTH_SHORT).show();

		startButton.setEnabled(false);
	}

	@Override
	public void onPeersChanged() {
		// Empty on purpose
		Toast.makeText(this, "Peer list changed", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onNetworkMembershipChanged(final SimWifiP2pInfo info) {
		Toast.makeText(this, "Network membership changed", Toast.LENGTH_SHORT).show();

		// get group info (device names and corresponding sockets)
		P2pApplication.getInstance().requestInGroup(new SimWifiP2pManager.GroupInfoListener() {
			@Override
			public void onGroupInfoAvailable(final SimWifiP2pDeviceList devices, final SimWifiP2pInfo groupInfo) {
				deviceList = devices;
			}
		});

		// update list view
		devicesAdapter.clear();
		devicesAdapter.addAll(info.getDevicesInNetwork());
		devicesAdapter.notifyDataSetChanged();
	}

	@Override
	public void onGroupOwnershipChanged(final SimWifiP2pInfo info) {
		Toast.makeText(this, "Group ownership changed", Toast.LENGTH_SHORT).show();

		if (info.askIsGO()) {
			startButton.setEnabled(true);
		}
	}
}
