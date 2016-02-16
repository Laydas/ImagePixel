package com.example.iain.imagepixel;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class SendWord extends AppCompatActivity {

    Button btnSend;
    private SeekBar seekSpeed;
    private TextView textSpeed;
    int speedSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_word);
        initializeVariables();
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        // Initialize textview with 0
        textSpeed.setText("Speed: 1");

        seekSpeed.setMax(49);

        seekSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int value = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value = (progress + 1);
                speedSend = (((progress -49) * -1) + 1) * 20;
                textSpeed.setText("Speed: " + value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    private void initializeVariables(){
        seekSpeed = (SeekBar) findViewById(R.id.seekSpeed);
        textSpeed = (TextView) findViewById(R.id.textSpeed);
    }
}
