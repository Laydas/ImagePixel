package com.example.iain.imagepixel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

public class MainActivity extends AppCompatActivity {

    private boolean onImage = true;

    private BluetoothAdapter myBluetoothAdapter;
    ListView myListView;
    ArrayAdapter BTArrayAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private ProgressDialog progress;
    String address = null;
    private boolean isBTConnected = false;
    BluetoothSocket btSocket = null;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private AlertDialog alertBT;

    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;
    String[][] blueSend = new String[32][16];

    ImageView imageView;

    private Bitmap bmp, operation;
    private BitmapDrawable abmp;
    private boolean pixeled = false;
    private ColorPicker cp;

    int colorRed, colorBlue, colorGreen;
    String sendColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        PixelFragment pixelFragment = new PixelFragment();

        transaction.add(R.id.fragment, pixelFragment);
        transaction.commit();

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //imageView = (ImageView) findViewById(R.id.imageView);
        abmp = (BitmapDrawable) getDrawable(R.drawable.starting_image);
        bmp = Bitmap.createScaledBitmap(abmp.getBitmap(), 384, 192, true);

        cp = new ColorPicker(this, 192,255,58);
        colorRed = 192;
        colorGreen = 255;
        colorBlue = 58;
        sendColor = "192255058";
    }

    public Bitmap pixelate(){
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

                // Turn the colour into a hex string.
                String rSend = Integer.toHexString(rAverage /= 144);
                String gSend = Integer.toHexString(gAverage /= 144);
                String bSend = Integer.toHexString(bAverage /= 144);
                aAverage /= 144;

                // Kill off the 2nd hex character if present
                if(rSend.length()== 2) {
                    rSend = rSend.substring(0, 1);
                } else {
                    rSend = "0";
                }
                if(bSend.length() == 2) {
                    bSend = bSend.substring(0,1);
                } else {
                    bSend = "0";
                }
                if(gSend.length() == 2) {
                    gSend = gSend.substring(0, 1);
                } else {
                    gSend = "0";
                }

                if(aAverage == 0) {
                    rSend = "N";
                }

                String sendString = rSend + "" + gSend + "" + bSend;
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
        //imageView.setImageBitmap(operation);
        pixeled = true;
        return operation;
    } // END pixelate function

    public void loadImageFromGallery() {
        // Create intent to open image applications like Galler, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
        pixeled = false;
    }

    public void sendImage() {
        if(btSocket != null && pixeled == true) {
            try {
                btSocket.getOutputStream().write("!!!".toString().getBytes());
                for(int j = 0; j < 16; j++) {
                    for (int i = 0; i < 32; i++) {
                        btSocket.getOutputStream().write(blueSend[i][j].getBytes());
                    }
                }
                try {Thread.sleep(25); }catch (InterruptedException e) {msg("Error");
                }
                btSocket.getOutputStream().write("~".toString().getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        } else if(pixeled == false) {
            msg("You Must Pixelate The Image First");
        } else {
            msg("No Bluetooth Device Connected!!");
        }
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
                // Set the image in imageview after decoding the string
                //imageView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
                bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(imgDecodableString), 384, 192, true);
                PixelFragment fragment = (PixelFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
                fragment.updateImage(bmp);
            } else {
                Toast.makeText(this, "You haven't picked an Image", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }

    public void sendWord(){
        WordFragment fragment = (WordFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        String sendSpeed = fragment.speedSend;
        String sendWord = fragment.getWord();
        // The string to send to the LED board is now in sendWord
        // All the color values will be in colorRed, Blue and Green
        // The scroll speed is now in speedVal.
        Log.v("TEST","!~!" + sendSpeed + "" + sendColor + "" + sendWord + "~");
        if(sendWord.length() < 1) {
            msg("Nothing entered, please enter some text first");
        } else {
            if(btSocket != null) {
                try {
                    btSocket.getOutputStream().write("!~!".getBytes());
                    btSocket.getOutputStream().write(sendSpeed.getBytes());
                    btSocket.getOutputStream().write(sendColor.getBytes());
                    btSocket.getOutputStream().write(sendWord.getBytes());
                    try {Thread.sleep(25); }catch (InterruptedException e) {msg("Error");
                    }
                    btSocket.getOutputStream().write("~".getBytes());
                } catch (IOException e) {
                    msg("Error");
                }
            }
            // Send string to arduino
        }
    }

    public void updateColor() {
        msg("updateColor");
        cp.show();

        Button okColor = (Button)cp.findViewById(R.id.okColorButton);
        okColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorRed = cp.getRed();
                colorGreen = cp.getGreen();
                colorBlue = cp.getBlue();
                // Compile a sendString of the colors rrrgggbbb always 9 digits long
                if (colorRed < 10) {
                    sendColor = "00" + colorRed;
                } else if (colorRed < 100) {
                    sendColor = "0" + colorRed;
                } else {
                    sendColor = "" + colorRed;
                }
                if (colorGreen < 10) {
                    sendColor += "00" + colorGreen;
                } else if (colorGreen < 100) {
                    sendColor += "0" + colorGreen;
                } else {
                    sendColor += "" + colorGreen;
                }
                if(colorBlue < 10) {
                    sendColor += "00" + colorBlue;
                } else if (colorBlue < 100) {
                    sendColor += "0" + colorBlue;
                } else {
                    sendColor += "" + colorBlue;
                }

                WordFragment fragment = (WordFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
                fragment.updateTextColor(cp.getColor());
                cp.dismiss();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id==R.id.action_bluetooth){
            if(myBluetoothAdapter == null) {
                msg("Bluetooth Device Not Available");
                finish();
            } else if(!myBluetoothAdapter.isEnabled()) {
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTon,1);
            } else {
                showBTDialog();
            }
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (id == R.id.action_word) {
            if(onImage){
                item.setTitle("Image");
                transaction.replace(R.id.fragment, new WordFragment());
                msg("Send Word");
                onImage = false;
            } else {
                item.setTitle("Word");
                transaction.replace(R.id.fragment, new PixelFragment());
                msg("Send Image");
                onImage = true;
            }
            transaction.addToBackStack(null);
            transaction.commit();

            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            msg("TESTING");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    public void showBTDialog() {
        AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View viewLayout = inflater.inflate(R.layout.bt_list, (ViewGroup) findViewById(R.id.bt_list));

        popDialog.setTitle("Paired Bluetooth Devices");
        popDialog.setView(viewLayout);

        myListView = (ListView) viewLayout.findViewById(R.id.BTList);
        BTArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        myListView.setAdapter(BTArrayAdapter);

        pairedDevices = myBluetoothAdapter.getBondedDevices();

        if(pairedDevices.size()>0) {
            for (BluetoothDevice device : pairedDevices)
                BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
        } else {
            msg("No paired bluetooth devices found");
        }

        myListView.setOnItemClickListener(myListClickListener);
        popDialog.setPositiveButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertBT = popDialog.create();
        alertBT.show();
    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            String info = ((TextView) v).getText().toString();
            address = info.substring(info.length() - 17);
            new ConnectBT().execute();
        }
    };

    @Override
    public final void onBackPressed() {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(onImage){
            finish();
        } else {
            onImage = true;
            msg("Send Image");
            transaction.replace(R.id.fragment, new PixelFragment());
            transaction.commit();
        }
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(MainActivity.this, "Connecting...", "Please wait!!!");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if(btSocket == null || !isBTConnected) {

                    myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice btDevice = myBluetoothAdapter.getRemoteDevice(address);
                    btSocket = btDevice.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(!ConnectSuccess) {
                msg("Connectiong Failed, please try again");
            } else {
                msg("Connected.");
                isBTConnected = true;
                ActionMenuItemView item = (ActionMenuItemView)findViewById(R.id.action_bluetooth);
                item.setTitle("Connected");
                alertBT.dismiss();
            }
            progress.dismiss();
        }
    }
}