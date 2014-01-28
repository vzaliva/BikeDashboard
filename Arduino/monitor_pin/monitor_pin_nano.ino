#include <avr/io.h>
#include <avr/interrupt.h>

volatile int last = 0;
volatile unsigned int serial = 0;

// This is the INT1 Pin of the ATmega328
int sensePin = 3;

// Install the interrupt routine.
ISR(INT1_vect) {
  int value = digitalRead(sensePin);
  if(value!=last)
  {
    if (value) {
      serial++;
    }
  }
  last = value;
}

void setup() {
  Serial.begin(9600);
  Serial.println("Initializing ihandler");
  // Global Enable INT0 interrupt
  DDRD &= ~(1 << DDD3);     // Clear the PD2 pin
  // PD2 (PCINT0 pin) is now an input
  PORTD |= (1 << PORTD3);    // turn On the Pull-up
  // PD2 is now an input with pull-up enabled
  EICRA |= (1 << ISC01);    // set INT0 to trigger on ANY logic change
  EIMSK |= (1 << INT1);     // Turns on INT0
  sei();                    // turn on interrupts
  Serial.println("Finished initialization");
}

void loop() {
  int prev = 0;
  while(1)
  {
    int next = serial;
    if(prev<next)
    {
      prev++;
      Serial.println(prev);
    }
  }
}

