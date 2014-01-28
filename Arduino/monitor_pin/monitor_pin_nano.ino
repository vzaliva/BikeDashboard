// Definition of interrupt names
#include < avr/io.h >
// ISR interrupt service routine
#include < avr/interrupt.h >

volatile int last = 0;
volatile unsigned int serial=0;

// This is the INT0 Pin of the ATmega328 (Arduino Nano 3.x)
int sensePin = 3;

// Install the interrupt routine.
ISR(INT0_vect) {  
  int value = digitalRead(sensePin);
  if(value!=last)
  {
    if (value) {
      Serial.println(serial);
      serial++;
    }
  }
  last = value;
}


void setup() {
  Serial.begin(9600);
  Serial.println("Initializing ihandler");
  pinMode(sensePin, INPUT);
  Serial.println("Processing initialization");
  // Global Enable INT0 interrupt
  EIMSK |= ( 1 << INT0);
  // Signal change triggers interrupt
  EICRA |= ( 1 << ISC00);
  EICRA |= ( 0 << ISC01);
  Serial.println("Finished initialization");
}

void loop() {
  //delay(100);    
}

