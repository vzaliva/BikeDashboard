
volatile unsigned int serial=0;

// This is the INT0 Pin of the ATmega328 (Arduino Nano 3.x)
int sensePin = 3;
int ledPin = 13;

// Install the interrupt routine.
void handler() {
  Serial.println(serial);
  serial++;
}

void setup() {
  Serial.begin(9600);
  Serial.println("Initializing ihandler");
  pinMode(sensePin, INPUT);
  pinMode(ledPin, OUTPUT);
  Serial.println("Processing initialization");
  attachInterrupt(0, handler, CHANGE);
  Serial.println("Finished initialization");
}

void loop() {
  //delay(100);
  //digitalWrite(ledPin, serial & 1);

}

