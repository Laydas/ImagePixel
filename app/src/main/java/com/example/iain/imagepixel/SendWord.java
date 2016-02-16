package com.example.iain.imagepixel;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;


public class SendWord extends AppCompatActivity {

    Button btnSend, btnColor;
    private SeekBar seekSpeed;
    private TextView textSpeed, textColor;
    int speedSend, colorRed, colorBlue, colorGreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_word);
        initializeVariables();
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        final ColorPicker cp = new ColorPicker(SendWord.this, 192,255,58);


        btnColor = (Button) findViewById(R.id.btn_Color);
        btnSend = (Button) findViewById(R.id.btn_sendWord);
        textColor = (TextView) findViewById(R.id.textCOLOUR);

        textColor.setTextColor(0xffc0ff3a);


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendWord();
            }
        });

        btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cp.show();

                Button okColor = (Button)cp.findViewById(R.id.okColorButton);
                okColor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        colorRed = cp.getRed();
                        colorGreen = cp.getGreen();
                        colorBlue = cp.getBlue();

                        textColor.setTextColor(cp.getColor());
                        cp.dismiss();
                    }
                });
            }
        });
        // Initialize text view with 0
        textSpeed.setText("1");
        seekSpeed.setMax(49);


        seekSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int value = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value = (progress + 1);
                speedSend = (((progress - 49) * -1) + 1) * 20;
                textSpeed.setText("" + value);
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

    private void sendWord() {

    }
}
