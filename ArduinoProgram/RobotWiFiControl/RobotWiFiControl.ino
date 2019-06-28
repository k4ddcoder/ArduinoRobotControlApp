
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
  Serial.println("Robot ctrl");

  if(server.args() == 2)
  {
    if(server.arg("vel") == "" or server.arg("rot") == "")
    {
      server.send(400, "text/plain", "empty fields");
    }else {

      // motor update
      int vel = server.arg("vel");
      int rot = server.arg("rot");

      server.send(200, "text/plain", "Done");
      
    }
    
    
  }else {

    server.send(400, "text/plain", "incorrect nArgs");
  }
  
  String message = "Number of args received: ";
  message += server.args();
  message += "\n";

  for(int i = 0; i < server.args(); i++)
  {
    message += "Arg nº" + (String)i + "->";
    message += server.argName(i) + ": ";
    message += server.arg(i) + "\n";
  }

  Serial.println(message);
  server.send(200, "text/plain", message);
}
void setup()
{ 
  
  Serial.begin(9600);
  Serial.println();
    
  // initial settings of pins
  pinMode(RightMotorSpeed, OUTPUT);
  pinMode(LeftMotorSpeed, OUTPUT);
  pinMode(RightMotorDir, OUTPUT);
  pinMode(LeftMotorDir, OUTPUT);

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
