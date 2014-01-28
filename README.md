BikeDashboard
=============

Dashboard for stationary bike.

Designed to measure RPM and calculate burned calories on [FitDesk](fitdesk.jpg) stationry bike.

There are two versions of hardware design using two different Arduino
boards. You can use either design:


Design 1: [Arduino Uno](http://arduino.cc/en/Main/ArduinoBoardUno) is
used with 10K pull-up resistor.  See
[schematics](Arduino/schematics.png) for hardware interfacing details
and [Arduno folder](Arduino/monitor_pin/) for firmware files.

Design 2: [Arduino Nano](http://arduino.cc/en/Main/arduinoBoardNano)
is used. No external pull-up resistor required. All you need to
connect your bike to GND and D3 pins. This is simpler and more compact
design. It could be mounted in this
[3D-printed Arduino Nano case](http://www.thingiverse.com/thing:178175).

The software submits workout results to FitBit web site using their
API and will be visible in their
[activity log](https://www.fitbit.com/activities).

Requirements (stuff you need):

  * [Arduino Uno](http://arduino.cc/en/Main/ArduinoBoardUno) or [Arduino Nano](http://arduino.cc/en/Main/arduinoBoardNano)
  * USB A Male to B Male Cable [(like this)](http://www.amazon.com/Mediabridge-2-0-Male-Cable-Feet/dp/B001MSZBNA/)
  * 10K resistor
  * Female mono jack [(like this)](http://www.amazon.com/gp/product/B000067RC4/)
  * [FitDesk 2.0](http://fitdesk.net/)
  * Java 7 JDK (not JRE!)

(Currently tested only under MacOS)

Screenshot:

![Screenshot](/screenshot.png)
