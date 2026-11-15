package com.example.cse489a2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

// Second screen.
// Custom option: takes a text message and passes it to the third activity.
// Battery option: shows the battery percentage from the system broadcast.
public class SecondActivity extends AppCompatActivity {

    public static final String EXTRA_TYPE = "type";
    public static final int TYPE_CUSTOM = 0;
    public static final int TYPE_BATTERY = 1;

    private int type;
    private TextView batteryText;
    private BroadcastReceiver batteryReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        type = getIntent().getIntExtra(EXTRA_TYPE, TYPE_CUSTOM);

        TextView heading = findViewById(R.id.text_heading);
        EditText input = findViewById(R.id.input_text);
        batteryText = findViewById(R.id.text_battery);

        if (type == TYPE_CUSTOM) {
            heading.setText(R.string.custom_heading);
            input.setVisibility(View.VISIBLE);
            batteryText.setVisibility(View.GONE);
        } else {
            heading.setText(R.string.battery_heading);
            input.setVisibility(View.GONE);
            batteryText.setVisibility(View.VISIBLE);
        }

        findViewById(R.id.btn_next).setOnClickListener(v -> {
            String message = input.getText().toString().trim();
            if (type == TYPE_CUSTOM && message.isEmpty()) {
                Toast.makeText(this, R.string.enter_message, Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, ThirdActivity.class);
            intent.putExtra(EXTRA_TYPE, type);
            intent.putExtra(ThirdActivity.EXTRA_MESSAGE, message);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (type == TYPE_BATTERY) {
            batteryReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                    if (level >= 0 && scale > 0) {
                        int percent = level * 100 / scale;
                        batteryText.setText("Battery: " + percent + "%");
                    }
                }
            };
            // ACTION_BATTERY_CHANGED is sticky, so we get the current value right away
            ContextCompat.registerReceiver(this, batteryReceiver,
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED),
                    ContextCompat.RECEIVER_EXPORTED);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (batteryReceiver != null) {
            unregisterReceiver(batteryReceiver);
            batteryReceiver = null;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
