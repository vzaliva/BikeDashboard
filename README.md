BikeDashboard
=============

Dashboard for stationary bike.

Designed to measure RPM and calculate burned calories on [FitDesk](fitdesk.jpg) stationry bike.
Using Arduino Uno as hadware interface. See [schematics](Arduino/schematics.png) for hardware interfacing details and [Arduno folder](Arduino/monitor_pin/) for firmware files.

The software submits workout results to FitBit web site using their API and will be visible in their [activity log](https://www.fitbit.com/activities).

Requirements (stuff you need):

  * [Arduino Uno](http://arduino.cc/en/Main/ArduinoBoardUno)
  * USB A Male to B Male Cable [(like this)](http://www.amazon.com/Mediabridge-2-0-Male-Cable-Feet/dp/B001MSZBNA/)
  * 10K resistor
  * Female mono jack [(like this)](http://www.amazon.com/gp/product/B000067RC4/)
  * [FitDesk 2.0](http://fitdesk.net/)
  * Java 7 JDK (not JRE!)

(Currently tested only under MacOS)

Screenshot:

![Screenshot](/screenshot.png)
