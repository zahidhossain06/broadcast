package com.example.cse489a2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

// Third screen.
// Custom option: registers our own broadcast receiver and shows the message it gets.
// Battery option: does nothing here.
public class ThirdActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "message";
    public static final String ACTION_CUSTOM = "com.example.cse489a2.CUSTOM_BROADCAST";

    private BroadcastReceiver customReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView info = findViewById(R.id.text_info);
        TextView received = findViewById(R.id.text_received);
        Button send = findViewById(R.id.btn_send);

        int type = getIntent().getIntExtra(SecondActivity.EXTRA_TYPE, SecondActivity.TYPE_CUSTOM);
        if (type == SecondActivity.TYPE_BATTERY) {
            // second option: nothing to do here
            info.setText(R.string.third_nothing);
            send.setVisibility(View.GONE);
            received.setVisibility(View.GONE);
            return;
        }

        String message = getIntent().getStringExtra(EXTRA_MESSAGE);
        info.setText(getString(R.string.third_waiting) + "\n" + message);

        // our custom broadcast receiver
        customReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String text = intent.getStringExtra(EXTRA_MESSAGE);
                received.setText("Broadcast received:\n" + text);
            }
        };
        ContextCompat.registerReceiver(this, customReceiver,
                new IntentFilter(ACTION_CUSTOM), ContextCompat.RECEIVER_NOT_EXPORTED);

        // send the text as a broadcast to ourselves
        send.setOnClickListener(v -> {
            Intent broadcast = new Intent(ACTION_CUSTOM);
            broadcast.setPackage(getPackageName());
            broadcast.putExtra(EXTRA_MESSAGE, message);
            sendBroadcast(broadcast);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (customReceiver != null) {
            unregisterReceiver(customReceiver);
            customReceiver = null;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
