package com.cmov.bomberman.controller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cmov.bomberman.R;

import org.w3c.dom.Text;

import java.util.LinkedList;
import java.util.List;

public class CreateServerActivity extends Activity implements WifiP2PListener {

    final String TAG = getClass().getSimpleName();

    String myUsername;
    Handler handler = new Handler();
    List<String> myPlayers = new LinkedList<String>();
    ListView listView;
    int clickedItemPosition;
    Runnable updateMyPlayersThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_server);
        Typeface blockFonts = Typeface.createFromAsset(getAssets(), "Inconsolata-Regular.ttf");
        TextView bombermanTxt = (TextView) findViewById(R.id.bombermantxt);
        TextView playersGame = (TextView) findViewById(R.id.playersGame);
        Button startGameBtn = (Button) findViewById(R.id.startgamebtn);
        bombermanTxt.setTypeface(blockFonts);
        playersGame.setTypeface(blockFonts);
        startGameBtn.setTypeface(blockFonts);

        Bundle extras = getIntent().getExtras();
        myUsername = (String) extras.get("username");

        ApplicationP2PInfo.mReceiver.setWifiP2PListener(this);
        ApplicationP2PInfo.createP2PGroup(TAG);
        updateMyPlayersThread = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    ApplicationP2PInfo.requestP2PGroupInfo();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onUpdateListView(ApplicationP2PInfo.groupClientsNames);
                        }
                    });
                }

            }
        };
    }

    // metodo que mostra a minha lista de players

    @Override
    public void onUpdateListView(List<String> list) {
        myPlayers = list;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, myPlayers);

        // Assign adapter to ListView
        listView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
    }

    // metodo starGame para botao
    public void onClickStartGame() {

        ApplicationP2PInfo.connectToP2POwner(TAG);

        Intent intent = new Intent(CreateServerActivity.this, MultiPlayerGameActivity.class);
        intent.putExtra("isServer", true);
        intent.putExtra("username", myUsername);
        intent.putExtra("hostname", ApplicationP2PInfo.groupOwnerAddress.getHostAddress());
        intent.putExtra("level", 1);
        startActivity(intent);
    }
}
