package com.example.iain.imagepixel;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;
    String[][] blueSend = new String[32][16];

    // Declare the button b1 and ImageView im
    Button b1;
    ImageView im;

    // Declare the bitmaps bmp and operation
    private Bitmap bmp;
    private Bitmap operation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create
        b1 = (Button) findViewById(R.id.button);
        im = (ImageView) findViewById(R.id.imageView);

        BitmapDrawable abmp = (BitmapDrawable) im.getDrawable();
        bmp = Bitmap.createScaledBitmap(abmp.getBitmap(), 384, 192, true);
    }

    public void loadImagefromGallery(View view) {
        // Create intent to open image applications like Galler, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an image is loaded
            if(requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
                // Get image from data
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                ImageView imgView = (ImageView) findViewById(R.id.imageView);
                // Set the image in imageview after decoding the string
                imgView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
            } else {
                Toast.makeText(this, "You haven't pick Image", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
        im = (ImageView) findViewById(R.id.imageView);

        BitmapDrawable abmp = (BitmapDrawable) im.getDrawable();
        bmp = Bitmap.createScaledBitmap(abmp.getBitmap(), 384, 192, true);

    }

    // The function that pixelates the images
    public void pixelate(View view){
        // Get the bitmap
        operation= Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(),bmp.getConfig());
                // Figure out how many pixels wide by tall it is
        // 12x12 is the number of pixels

        // Step through each block of pixels so the steps are 32 horizontal by 16 vertical
        for(int i=0; i < 32; i++){
            for(int j=0; j < 16; j++){
                // Initialize the average color
                int rAverage = 0;
                int gAverage = 0;
                int bAverage = 0;
                int aAverage = 0;

                // Step through all the pixels inside one block and add all color values together
                for(int x = i * 12; x < (i * 12) + 11 ; x++) {
                    for(int y = j * 12; y < (j * 12) + 11; y++) {
                        int p = bmp.getPixel(x, y);
                        rAverage += Color.red(p);
                        gAverage += Color.green(p);
                        bAverage += Color.blue(p);
                        aAverage += Color.alpha(p);
                    }
                }

                // Divide all the colour values by the number of pixels in a block
                rAverage = rAverage / 144;
                bAverage = bAverage / 144;
                gAverage = gAverage / 144;

                // Assign the colors into a string array !!RED:GREEN:BLUE~
                String sendString = "!!" + i + ":" + j + ":" + rAverage + ":" + gAverage + ":" + bAverage + "~";
                blueSend[i][j] = sendString;

                // Write the average color values to each pixel in the block
                for(int x = i * 12; x < (i * 12) + 11 ; x++) {
                    for(int y = j * 12; y < (j * 12) + 11; y++) {
                        operation.setPixel(x, y, Color.argb(255, rAverage, gAverage, bAverage));
                    }
                }
            }
        }
        // Push image back to the screen
        im.setImageBitmap(operation);
    } // END pixelate function

    public void bluetoothSend(View view) {
        Log.v("TESTING", "SEND TEST: " + blueSend[0][0] + "~~" + blueSend[0][1]);
    }

}