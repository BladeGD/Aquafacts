//Code sections taken from the following sources and modified by Teng Zhao and Group 7 for COEN 390 Winter 2021 semester.
//Serial communication with NodeMCU: https://engineeringprojectshub.com/serial-communication-between-nodemcu-and-arduino/
//Firebase arduino communication library: https://github.com/FirebaseExtended/firebase-arduino

#include <FirebaseArduino.h>
#include <ESP8266WiFi.h>

#define FIREBASE_HOST ""
#define FIREBASE_AUTH ""

#define WIFI_SSID ""
#define WIFI_PASSWORD ""

DynamicJsonBuffer jsonBuffer;

String myString;// message from Arduino w/ sensor data
char rdata;// received characters

float tempValue;
float phValue;
float salinityValue;

void setup() {
  Serial.begin(9600);
  delay(1000);
  
  connectWifi();
  connectFirebase();
}

void loop() {
  if (Serial.available() > 0 ) {
    rdata = Serial.read(); 
    myString = myString+ rdata; 
    //Serial.print(rdata);
    if( rdata == '\n'){

      Serial.print("--------------");
      Serial.println();
      Serial.print("myString: ");
      Serial.print(myString);
      
      String temp = getValue(myString, ',', 0);
      String ph = getValue(myString, ',', 1);
      String salinity = getValue(myString, ',', 2); 
      
      tempValue = temp.toFloat();
      phValue = ph.toFloat();
      salinityValue = salinity.toFloat();
      
      myString = "";
    }
  }
  
  if (Serial.available() == 0 && tempValue > 0){

    Serial.print("--------------");
    Serial.println();

    Serial.print("TEMPERATURE = ");
    Serial.print(tempValue);
    Serial.println();
    
    Serial.print("pH = ");
    Serial.print(phValue);
    Serial.println();
    
    Serial.print("SALINITY = ");
    Serial.print(salinityValue);
    Serial.println();
    
    JsonObject& dataObj = jsonBuffer.createObject();
    JsonObject& tempTime = dataObj.createNestedObject("timestamp");
    dataObj["temperature"] = tempValue;
    dataObj["ph"] = phValue;
    dataObj["salinity"] = salinityValue;
    tempTime[".sv"] = "timestamp";
    
    if(dataObj.containsKey("timestamp")){
      Firebase.push("/UniqueID3/Data", dataObj);
      Serial.println("Pushed to DB!");  
      //delay(900000); //15 min
      //delay(60000); //1 min
      delay(30000); //30 sec
      //delay(1000); //1 sec
    }
  } 
}

String getValue(String data, char separator, int index) {
    int found = 0;
    int strIndex[] = { 0, -1 };
    int maxIndex = data.length() - 1;
 
    for (int i = 0; i <= maxIndex && found <= index; i++) {
        if (data.charAt(i) == separator || i == maxIndex) {
            found++;
            strIndex[0] = strIndex[1] + 1;
            strIndex[1] = (i == maxIndex) ? i+1 : i;
        }
    }
    return found > index ? data.substring(strIndex[0], strIndex[1]) : "";
}

void connectWifi(){
  //try to connect with wifi
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to ");
  Serial.print(WIFI_SSID);
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.print("Connected to ");
  Serial.println(WIFI_SSID);
  //print local IP address
  Serial.print("IP Address is : ");
  Serial.println(WiFi.localIP());
}

void connectFirebase(){
  //connect to firebase
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
}
