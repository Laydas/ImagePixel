package com.example.iain.imagepixel;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


public class PixelFragment extends Fragment{

    Bitmap bmp;
    Button btnSend, btnLoad, btnPixel;
    ImageView imageView;

    public PixelFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pixel, container, false);

        imageView = (ImageView) view.findViewById(R.id.imageView);
        btnPixel = (Button) view.findViewById(R.id.btn_pixel);
        btnLoad = (Button) view.findViewById(R.id.btn_load);
        btnSend = (Button) view.findViewById(R.id.btn_send);

        BitmapDrawable abmp = (BitmapDrawable) imageView.getDrawable();
        bmp = Bitmap.createScaledBitmap(abmp.getBitmap(), 384, 192, true);

        btnPixel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bmp = ((MainActivity)getActivity()).pixelate();
                imageView.setImageBitmap(bmp);
            }
        });
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).loadImageFromGallery();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).sendImage();
            }
        });

        return view;
    }

    public void updateImage(Bitmap bmp) {
        imageView.setImageBitmap(bmp);
    }
}