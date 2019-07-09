
// Load Wi-Fi library
#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>

// Pin specification of motors
#define RightMotorSpeed 5
#define RightMotorDir 0
#define LeftMotorSpeed 4
#define LeftMotorDir 2

// IP add of the soft access point, gateway IP addr, subnet mask
IPAddress local_IP(192,168,4,55);
IPAddress gateway(192,168,4,1);
IPAddress subnet(255,255,255,0);
ESP8266WebServer server(80);


void handleRobot()
{

  if(server.args() == 2)
  {
    // motor update
    int velL = server.arg("velL").toInt();
    int velR = server.arg("velR").toInt();

    Serial.println("velL: " + server.arg("velL") + " velR: " + server.arg("velR"));

    if(velR < 0) {
      digitalWrite(RightMotorDir, LOW);
      velR = velR * -1;
    }else {
      digitalWrite(RightMotorDir, HIGH);
    }

    if(velL < 0) {
      digitalWrite(LeftMotorDir, LOW);
      velL = velL * -1;
    }else {
      digitalWrite(LeftMotorDir, HIGH);
    }
    
    analogWrite(RightMotorSpeed, velR);
    analogWrite(LeftMotorSpeed, velL);

    server.send(200, "text/plain", "Done");
    
  }else {

    server.send(400, "text/plain", "incorrect nArgs");
  }
  
}
void setup()
{ 
  
  Serial.begin(115200);
  Serial.println();

  // initial settings of pins
  pinMode(RightMotorSpeed, OUTPUT);
  pinMode(LeftMotorSpeed, OUTPUT);
  pinMode(RightMotorDir, OUTPUT);
  pinMode(LeftMotorDir, OUTPUT);

  analogWriteFreq(50000);
  
  // initial settings for direction and speed
  digitalWrite(RightMotorSpeed, LOW);
  digitalWrite(LeftMotorSpeed, LOW);
  digitalWrite(RightMotorDir, HIGH);
  digitalWrite(LeftMotorDir,HIGH);

  // setting softAP configuration
  WiFi.softAPConfig(local_IP, gateway, subnet);
  WiFi.softAP("ROBOT_HOTSPOT", "12345678", 1, false, 1);

  // softAP IP addr
  Serial.print("SoftAP IP addr: ");
  Serial.println(WiFi.softAPIP());

  // server handle on /robotctrl
  server.on("/robotctrl", handleRobot);
  // server start on port 80 (HTTP)
  server.begin();
  
}



void loop()
{
  server.handleClient();
}
