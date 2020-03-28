#include <Arduino.h>

#include <ESP8266WiFi.h>
#include <WebSocketsServer.h>
#include <Hash.h>


WebSocketsServer webSocket = WebSocketsServer(81);

#define USE_SERIAL Serial
#define ssid "ESPap"
#define password "qwerty123"
int throttle = 0,yaw = 0,pitch = 0,roll = 0;
String d = "";
int *data;

void webSocketEvent(uint8_t num, WStype_t type, uint8_t * payload, size_t length) {

    switch(type) {
        case WStype_DISCONNECTED:
            USE_SERIAL.printf("[%u] Disconnected!\n", num);
            break;
        case WStype_CONNECTED:
            {
                IPAddress ip = webSocket.remoteIP(num);
                USE_SERIAL.printf("[%u] Connected from %d.%d.%d.%d url: %s\n", num, ip[0], ip[1], ip[2], ip[3], payload);
        
        // send message to client
        //webSocket.sendTXT(num, "Connected");
            }
            break;
        case WStype_TEXT:
            //USE_SERIAL.printf("[%u] get Text: %s\n", num, payload);
            
            d = (char*)payload;
            data = split(d);
            Serial.printf("Yaw=%d\tThrottle=%d\tRoll=%d\tPitch=%d\n",data[0],data[1],data[2],data[3]);
            
            yaw = map(data[0],-100,100,0,255);
            throttle = map(data[1],0,200,0,255);
            roll = map(data[2],-100,100,0,255);
            pitch = map(data[3],-100,100,0,255);
            
            free(data);
            free(d);
            
            // send message to client
            
           webSocket.sendTXT(num, "Received");

            // send data to all connected clients
            // webSocket.broadcastTXT("message here");
            break;
        case WStype_BIN:
            USE_SERIAL.printf("[%u] get binary length: %u\n", num, length);
            hexdump(payload, length);

            // send message to client
            // webSocket.sendBIN(num, payload, length);
            break;
    }

}

void setup() {
    // USE_SERIAL.begin(921600);
    USE_SERIAL.begin(115200);

    //Serial.setDebugOutput(true);
    USE_SERIAL.setDebugOutput(true);

    USE_SERIAL.println();
    USE_SERIAL.println();
    USE_SERIAL.println();

    for(uint8_t t = 4; t > 0; t--) {
        USE_SERIAL.println("[SETUP] BOOT WAIT ");
        USE_SERIAL.flush();
        delay(1000);
    }


    Serial.print("Configuring access point...");
    WiFi.softAP(ssid, password);

    IPAddress myIP = WiFi.softAPIP();
    Serial.print("AP IP address: ");
    Serial.println(myIP);

    webSocket.begin();
    webSocket.onEvent(webSocketEvent);
}

int* split(String str)
{
  int* d = (int *)malloc(4*sizeof(int));
   int j = 0;
  String c = "";
  for(int i=0;i<str.length();i++)
  {
    if(str[i]==';')
    {
      d[j] = c.toInt();
      c = "";
      j++;
    }
    else
    {
      c = c+str[i];
    }
  }
 //Serial.printf("angle1=%d\tstr1=%d\tangle2=%d\tstr2=%d\n",d[0],d[1],d[2],d[3]);

  return d;
}

void loop() {
    webSocket.loop();
}
