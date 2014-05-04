package com.cmov.bomberman.controller.net;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cmov.bomberman.R;

import java.util.ArrayList;
import java.util.List;

public class ServerWaitingFragment extends Fragment {

    //private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    View mContentView = null;
    private WifiP2pDevice device;
    private int levelGame;
    List<String> usernamePlayers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_server_waiting, null);
        return mContentView;
    }

    // method called when start button is pressed
    public void startGame(View view) {
        // chama funcao na activity
        // ((MultiplayerGameActivity)getActivity()).instantiateGame(levelGame, usernamePlayers, "host", "port");
    }
}
