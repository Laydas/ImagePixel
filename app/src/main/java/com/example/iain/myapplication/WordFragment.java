package com.example.iain.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Iain on 2/18/2016.
 */
public class WordFragment extends Fragment{

    Button btnSendWord, btnColor;
    private SeekBar seekSpeed;
    private TextView textSpeed, textColor;
    int speedSend, seekVal;

    public WordFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word, container, false);

        btnSendWord = (Button)view.findViewById(R.id.btn_sendWord);
        btnColor = (Button)view.findViewById(R.id.btn_Color);
        textColor = (TextView) view.findViewById(R.id.textCOLOUR);
        textColor.setTextColor(0xffc0ff3a);
        textSpeed = (TextView) view.findViewById(R.id.textSpeed);
        seekSpeed = (SeekBar) view.findViewById(R.id.seekSpeed);

        textSpeed.setText("1");
        seekSpeed.setMax(49);

        seekSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekVal = (progress + 1);
                speedSend = (((progress - 49) * -1) + 1) * 20;
                textSpeed.setText("" + seekVal);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnSendWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).sendWord();
            }
        });
        btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).updateColor();
            }
        });
        return view;
    }

    public void updateTextColor(int color) {
        textColor.setTextColor(color);
    }
}