#include<Servo.h>
Servo s1;
void setup() {
  // put your setup code here, to run once:
  pinMode(D6,OUTPUT);
  s1.attach(D6);
  Serial.begin(115200);


}

void loop() {
  // put your main code here, to run repeatedly:
  if(Serial.available()>0)
  {
    String k = Serial.readString();
    int num = k.toInt();
    Serial.println(num);
    s1.writeMicroseconds(num);
  }

}
