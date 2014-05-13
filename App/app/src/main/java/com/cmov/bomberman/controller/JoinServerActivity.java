package com.cmov.bomberman.controller;

import android.content.Intent;
import android.graphics.Typeface;
import android.widget.AdapterView.OnItemClickListener;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmov.bomberman.R;

import java.util.LinkedList;
import java.util.List;

public class JoinServerActivity extends Activity implements WifiP2PListener {

    final String TAG = getClass().getSimpleName();
    String myUsername;
    ListView listView;
    int clickedItemPosition = -1;
    List<String> groupOwnerList = new LinkedList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_server);
        Typeface blockFonts = Typeface.createFromAsset(getAssets(), "Inconsolata-Regular.ttf");
        TextView bombermanTxt = (TextView) findViewById(R.id.bombermantxt);
        TextView choseServer = (TextView) findViewById(R.id.choseServers);
        Button joinServer = (Button) findViewById(R.id.joinServer);
        bombermanTxt.setTypeface(blockFonts);
        choseServer.setTypeface(blockFonts);
        joinServer.setTypeface(blockFonts);

        listView = (ListView) findViewById(R.id.serversList);
        Bundle extras = getIntent().getExtras();
        myUsername = (String) extras.get("username");

        ApplicationP2PInfo.mReceiver.setWifiP2PListener(this);
    }



    // method called when peers list change
    public void onUpdateListView(List<String> goList) {
        groupOwnerList = goList;


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, groupOwnerList);

        // Assign adapter to ListView
        listView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // ListView Clicked item index
                clickedItemPosition = position;
            }
        });
    }

    public void onClickStart() {

        if (clickedItemPosition >= 0) {
            String goChosen = (String) listView.getItemAtPosition(clickedItemPosition);
            ApplicationP2PInfo.connectToP2POwner(TAG);

            Intent intent = new Intent(JoinServerActivity.this, MultiPlayerGameActivity.class);
            intent.putExtra("isServer", false);
            intent.putExtra("username", myUsername);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "You must choose a server to join to",Toast.LENGTH_LONG);
        }
    }
}
