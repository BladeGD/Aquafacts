//Code sections taken from the following sources and modified by Teng Zhao and Group 7 for COEN 390 Winter 2021 semester.
//Temperature sensor: https://learn.adafruit.com/tmp36-temperature-sensor/using-a-temp-sensor
//pH sensor: https://circuitdigest.com/microcontroller-projects/arduino-ph-meter
//tds sensor: https://wiki.dfrobot.com/Gravity__Analog_TDS_Sensor___Meter_For_Arduino_SKU__SEN0244
//lcd display: https://www.arduino.cc/reference/en/libraries/liquidcrystal-i2c/
//Serial communication with NodeMCU: https://engineeringprojectshub.com/serial-communication-between-nodemcu-and-arduino/

#include <SoftwareSerial.h>
#include <EEPROM.h>
#include <LiquidCrystal_I2C.h>
#include <Wire.h>

SoftwareSerial nodemcu(2,3);

//Variables and definitions for pH sensor
int sensorPHValue = 0;
unsigned long int avgPHValue;
float bPH;
int bufPH[10],tempPH;
// -------------------

//Variables and definitions for Salinity sensor
#define salVREF 5.0 // analog reference voltage(Volt) of the ADC
#define salSCOUNT 30 // sum of sample point
int analogSalBuffer[salSCOUNT];// store the analog value in the array, read from ADC
int analogSalBufferTemp[salSCOUNT];
int analogSalBufferIndex = 0,copyIndex = 0;
float averageSalVoltage = 0,tdsValue = 0;
float salTemp = 25;
// -------------------

//for lcd
LiquidCrystal_I2C lcd(0x27, 2 , 1, 0, 4, 5, 6, 7, 3, POSITIVE);
// -------------------

const int tempSensor = A0;
const int phSensor = A1;
const int tdsSensor = A2;

float tempData; // temperature sensor data
float phData; // pH sensor data
float salData; // salinity sensor data

String sendData; //complete data, send to nodeMCU

void setup() {
  Serial.begin(9600);
  nodemcu.begin(9600);

  pinMode(tempSensor, INPUT);
  pinMode(phSensor, INPUT);
  pinMode(tdsSensor, INPUT);

  lcd.begin(20,4);
  //lcd.noBacklight();
}

void loop() {
  readTemp();
  readPH();
  readSalinity();

  sendData = sendData + tempData + "," + phData + "," + salData; // comma is delimiter
  
  Serial.println("----------");
  printTemp(); //prints temperature to serial monitor
  printPH(); //prints pH to serial monitor
  printSalinity(); //prints salinity to serial monitor

  if(tempData > 0 && phData > 0 && salData > 0){
    Serial.println("To nodeMCU: " + sendData); //prints what is sent to nodeMCU
    nodemcu.println(sendData);
  
    lcd.clear();
  
    lcd.setCursor(5,0);
    lcd.print("AquaFacts");
    
    printLCD();
  }else{
    Serial.println("Awaiting Data");
    lcd.clear();
  
    lcd.setCursor(4,0);
    lcd.print("Please Wait");
  }
  
  delay(1000);
  sendData = ""; //resets String
}

void readTemp(){
  float value = analogRead(tempSensor); //reads millivolts from sensor
  float mv = (value/1024.0)*5000;
  float celsius = mv/10; //converts millivolts to cesius
  tempData = celsius;
}

void readPH(){
  for(int i=0;i<10;i++){
    bufPH[i]=analogRead(phSensor);
    delay(500);
  }
  for(int i=0;i<9;i++){
    for(int j=i+1;j<10;j++){
      if(bufPH[i]>bufPH[j]){
        tempPH=bufPH[i];
        bufPH[i]=bufPH[j];
        bufPH[j]=tempPH;
        }
    }
  }
  avgPHValue=0;
  for(int i=2;i<8;i++)
    avgPHValue+=bufPH[i];
  float pHVol=(float)avgPHValue*5.0/1024/6;
  float phValue = -5.70 * pHVol + 21.34;
  phValue = phValue;
  if(phValue > 14){
    phValue = 14;
  }else if(phValue < 0){
    phValue = 0;
  }
  phData = phValue;
  //Serial.print("sensor = ");
  //Serial.println(phData);
  delay(20);
}

void readSalinity(){
  static unsigned long analogSampleTimepoint = millis();
   if(millis()-analogSampleTimepoint > 500U){ //every 40 milliseconds,read the analog value from the ADC
     analogSampleTimepoint = millis();
     analogSalBuffer[analogSalBufferIndex] = analogRead(tdsSensor); //read the analog value and store into the buffer
     analogSalBufferIndex++;
     if(analogSalBufferIndex == salSCOUNT) 
         analogSalBufferIndex = 0;
   }
   static unsigned long printTimepoint = millis();
   if(millis()-printTimepoint > 800U){
      printTimepoint = millis();
      for(copyIndex=0;copyIndex<salSCOUNT;copyIndex++)
        analogSalBufferTemp[copyIndex]= analogSalBuffer[copyIndex];
      averageSalVoltage = getMedianNum(analogSalBufferTemp,salSCOUNT) * (float)salVREF / 1024.0; //read the analog value more stable by the median filtering algorithm, and convert to voltage value
      float compensationCoefficient= 1.0 + 0.02*(tempData - 25.0); //temperature compensation formula: fFinalResult(25^C) = fFinalResult(current)/(1.0+0.02*(fTP-25.0));
      float compensationVoltage=averageSalVoltage / compensationCoefficient;//temperature compensation
      tdsValue=(133.42*compensationVoltage*compensationVoltage*compensationVoltage - 255.86*compensationVoltage*compensationVoltage + 857.39*compensationVoltage)*0.5; //convert voltage value to tds value
      salData = tdsValue;
   }
}

void printTemp(){ //prints temperature to serial monitor
  Serial.print("TEMPERATURE = ");
  Serial.print(tempData);
  Serial.print(" *C");
  Serial.println();
}

void printPH(){ //prints pH to serial monitor
  Serial.print("pH = ");
  Serial.print(phData);
  Serial.println();
}

void printSalinity(){ //prints salinity to serial monitor
  Serial.print("SALINITY = ");
  Serial.print(salData);
  Serial.print(" ppm");
  Serial.println();
}

int getMedianNum(int bArray[], int iFilterLen) {
  int bTab[iFilterLen];
  for (byte i = 0; i<iFilterLen; i++)
  bTab[i] = bArray[i];
  int i, j, bTemp;
  for (j = 0; j < iFilterLen - 1; j++) 
  {
  for (i = 0; i < iFilterLen - j - 1; i++) 
      {
    if (bTab[i] > bTab[i + 1]) 
        {
    bTemp = bTab[i];
        bTab[i] = bTab[i + 1];
    bTab[i + 1] = bTemp;
     }
  }
  }
  if ((iFilterLen & 1) > 0)
  bTemp = bTab[(iFilterLen - 1) / 2];
  else
  bTemp = (bTab[iFilterLen / 2] + bTab[iFilterLen / 2 - 1]) / 2;
  return bTemp;
}

void printLCD() {
  lcd.setCursor(0,1);
  printLCDTemp();
  lcd.setCursor(0,2);
  printLCDPH();
  lcd.setCursor(0,3);
  printLCDSalinity();
}
void printLCDTemp(){ //prints temperature to LCD
  lcd.print("TEMP = ");
  lcd.print(tempData);
  lcd.print(" *C");
}

void printLCDPH(){ //prints pH to LCD
  lcd.print("pH   = ");
  lcd.print(phData);
}

void printLCDSalinity(){ //prints salinity to LCD
  lcd.print("SAL  = ");
  lcd.print(salData);
  lcd.print(" ppm");
}
