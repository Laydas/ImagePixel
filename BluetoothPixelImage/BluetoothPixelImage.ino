/* -------------------------------------------------------
 * BluetoothPixelImage.ino
 * Created by Iain Letourneau iain.letourneau@gmail.com
 * Last updated on Feb 3rd 2016
 * -------------------------------------------------------
 * 
 * This is the Arduino side of the ImagePixel Android Application.
 * This sketch(program) sits in standby waiting for a string of hex
 * characters starting with !!! and ending with ~.  This string should
 * be 1540 characters long. 3 characters for each LED on the board (512)
 * plus the 4 characters added as a header and ending character.
 * 
 * Once the buffer has the correct starting and ending characters then
 * this program will step through all the characters 3 at a time. Each of
 * these 3 characters represents 0-F value for colour. This value will be
 * multiplied to create a simplified 0-255 range used by the LED board.
 * 
 * This process takes about 2-3 seconds after pressing send on the Android
 * device.
 * 
 * This has only been tested on an Arduino Mega 256 and with an Asus Zenfone 2
 */

//--------------------------------------------------------------------------
// INCLUDES
//--------------------------------------------------------------------------
// Include the Adafruit libraries for controlling the LED array
#include <Adafruit_GFX.h>
#include <Adafruit_NeoMatrix.h>
#include <Adafruit_NeoPixel.h>
//--------------------------------------------------------------------------

//--------------------------------------------------------------------------
// DEFINITIONS
//--------------------------------------------------------------------------
// Define the Arduino Pin for the LED matrix to be plugged into
#define LEDPIN 13
//--------------------------------------------------------------------------


//--------------------------------------------------------------------------
// MATRIX VARIABLES
//--------------------------------------------------------------------------
// Initialize the WS2812 (neopixel) array/matrix
Adafruit_NeoMatrix matrix = Adafruit_NeoMatrix(8, 8, 4, 2, LEDPIN,
                            NEO_MATRIX_TOP + NEO_MATRIX_LEFT +
                            NEO_MATRIX_ROWS + NEO_MATRIX_ZIGZAG +
                            NEO_TILE_TOP + NEO_TILE_LEFT +
                            NEO_TILE_ROWS + NEO_TILE_PROGRESSIVE,
                            NEO_GRB + NEO_KHZ800);
//--------------------------------------------------------------------------


//--------------------------------------------------------------------------
// DECLARE VARIABLES
//--------------------------------------------------------------------------
// initialize the bluetooth string for holding the incoming buffer
String blStr = "";
boolean wordSet = false;
String stringWord;
int red, green, blue;
int scrollSpeed;
unsigned long wordScrollTime = 0;
int delayInMillis = 200;
int wordLength = 0;
int scrollX = 0;
//--------------------------------------------------------------------------



//--------------------------------------------------------------------------
// VOID setup()
//--------------------------------------------------------------------------
void setup()
{
  // Start the serial for communicating with the console
  Serial.begin(9600);
  // Start bluetooth module serial
  Serial2.begin(9600);
  // Start the LED matrix object
  matrix.begin(); //initalize the matrix
  matrix.setTextWrap(false);
  // Max brightness is 255, I suggest setting the brightness at around 100 unless you have a lot of amps
  matrix.setBrightness(150);
  matrix.setTextColor(matrix.Color(255,0,0)); 
}
//--------------------------------------------------------------------------


//--------------------------------------------------------------------------
// INT char2int (CHAR d)
//--------------------------------------------------------------------------
/*
 * This function takes a single hex in the form of a char and turns it into
 * a digit from 0-15
 */
int char2int(char d) {
  // If the hex is from 0-9 then return an int for 0-9
  if (d >= '0' && d <= '9') {
    return d - '0';
  }
  d = tolower(d);
  // If the hex is from a-f then return an int from 10-15
  if (d >= 'a' && d <= 'f') {
    return (d - 'a') + 10;
  }
  return -1;
}
//--------------------------------------------------------------------------


//--------------------------------------------------------------------------
// VOID loop()
//--------------------------------------------------------------------------
void loop ()
{
  // Whenever the bluetooth is receiving data the arduino will push it into a buffer
  if(Serial2.available()){
    // Without the delay characters were being dropped ( I think )
    delay(50);
    while(Serial2.available()){
      char ch = Serial2.read();
      blStr += (ch);
      //Serial.println(ch);
    }
  }//----------------------------------------------------------------------

  // This section is for words sent from the phone not images
  if(blStr[0] == '!' && blStr[1] == '~' && blStr[2] == '!' && blStr[blStr.length() -1] == '~') {
    stringWord= blStr.substring(16,blStr.length()-1);  
    scrollSpeed = blStr.substring(3,7).toInt();
    red = blStr.substring(7,10).toInt();
    green = blStr.substring(10,13).toInt();
    blue = blStr.substring(13,16).toInt();
    
    
    wordSet = true;
    blStr = "";
    wordLength = stringWord.length() * 6;
    scrollX = 31;
    matrix.setTextColor(matrix.Color(red,green,blue));
  }

  // This is the section for incoming images
  // Check to see if the string has been properly formatted as such ("!!!" + data + "~")
  if(blStr[0] == '!' && blStr[1] == '!' && blStr[2] == '!' && blStr[blStr.length() -1] == '~'){
    matrix.fillScreen(0);
    // Initialize the color variables that will be used for drawing to the board
    // Initialize the finder to parse through the buffer
    int finder = 0;
    // Step into a 2D array to more easily assign the correct pixels

    for(int j = 0; j < 16; j++) {
        for(int i = 0; i < 32; i++) {
        // Get the current pixels appropriate RGB code
        finder = (((32 * j) + (i+1)) * 3);

        // Check to see if the alpha was 0 or not
        if(blStr[finder] == 'N'){
          // do something
        }
        // If the colour was set then pass that information into the matrix array
        else {
          // Pass the single hex char through the char2int function then into the color variables
          int red = char2int(blStr[finder]);
          int green = char2int(blStr[finder+1]);
          int blue = char2int(blStr[finder+2]);
          // As of now red will contain a single int from 0-15
  
          // Take the single int and turn it into a simplified 0-255 color value
          red = (red * 16) + red;
          green = (green * 16) + green;
          blue = (blue * 16) + blue;
          
          // Draw the current location(i,j) to the LED board with the current colours
          matrix.drawPixel(i,j,matrix.Color(red,green,blue));
        }
      }
    }
    // Clear out the buffer as we are done with it
    blStr = "";
    wordSet = false;
    // Update the board to show all the new LED colours
    matrix.show();
  }

  // Every 1000 ms check to see if the wordset is true and then show the word
  if(millis() - wordScrollTime >= delayInMillis) {
    if(wordSet == true){
      matrix.setCursor(scrollX, 0);
      matrix.fillScreen(0);
      matrix.print(stringWord);
      matrix.show();
      scrollX --;
      if(scrollX + wordLength == 0){
        wordSet = false;
      }
    }
    wordScrollTime = millis();
  }
}
