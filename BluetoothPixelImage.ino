//--------------------------------------------------------------------------
// INCLUDES
//--------------------------------------------------------------------------
#include <Adafruit_GFX.h>
#include <Adafruit_NeoMatrix.h>
#include <Adafruit_NeoPixel.h>
//--------------------------------------------------------------------------

//--------------------------------------------------------------------------
// DEFINITIONS
//--------------------------------------------------------------------------
#define TIMEOUT 1000
#define LEDPIN 13
//--------------------------------------------------------------------------


//--------------------------------------------------------------------------
// MATRIX VARIABLES
//--------------------------------------------------------------------------
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
String ledString = "";
boolean is_scroll = false;
int x = 1;
String blStr = "";
boolean string = false;
char *s;
//--------------------------------------------------------------------------



//--------------------------------------------------------------------------
// VOID setup()
//--------------------------------------------------------------------------
void setup()
{
  Serial.begin(9600);
  Serial2.begin(9600);
  matrix.begin(); //initalize the matrix
  matrix.setTextWrap(false);
  matrix.setBrightness(150);
  matrix.setTextColor(matrix.Color(0,255,0));
}
//--------------------------------------------------------------------------


//--------------------------------------------------------------------------
// VOID WriteSign(String toSign)
//--------------------------------------------------------------------------
void WriteSign (String toSign){
  matrix.fillScreen(0);
  matrix.setCursor(x,6);
  matrix.print(toSign);
  matrix.show();
}
//--------------------------------------------------------------------------



int char2int(char d) {
  if (d >= '0' && d <= '9') {
    return d - '0';
  }
  d = tolower(d);
  if (d >= 'a' && d <= 'f') {
    return (d - 'a') + 10;
  }
  return -1;
}

//--------------------------------------------------------------------------
// VOID loop()
//--------------------------------------------------------------------------
void loop ()
{
  // RECEIVE INPUT FROM blStr UNTIL ~ CHARACTER (no not let app send ~, use instead some weird code)
  if(Serial2.available()){
    delay(50);
    //Serial.println("Found");
    while(Serial2.available()){
      char ch = Serial2.read();
      blStr += (ch);
      if(blStr.indexOf("~") != -1){
      }
    }
  }//--------------------------------------------------------------------------------------------------

  // IF THE STRING IS FOR A PICTURE THEN PROCESS

  // TEST OUT MASSIVE CHAR 3D ARRAY
  if(blStr[0] == '!' && blStr[1] == '!' && blStr[2] == '!' && blStr[blStr.length() -1] == '~'){
    int red = 0;
    int green = 0;
    int blue = 0;
    int finder = 0;
    for(int i = 0; i < 32; i++) {
      for(int j = 0; j < 16; j++) {
        finder = ((32 * j) + i) * 3;
        
        red = char2int(blStr[finder]);
        green = char2int(blStr[finder+1]);
        blue = char2int(blStr[finder+2]);

        red = (red * 16) + red;
        green = (green * 16) + green;
        blue = (blue * 16) + blue;

        String debug = String(i) + "-" + String(j) + ":" +String(red) + ":" + String(green) + ":" + String(blue);
        Serial.println(debug);
        matrix.drawPixel(i,j,matrix.Color(red,green,blue));
      }
    }

    // Clear out the bluetooth string
    blStr = "";
    matrix.show();
  }
}
