#!/usr/bin/env python

# requires PySerial. On MacOS (port install py-serial)

DEV='/dev/tty.usbmodem1411' # Uno
DEV='/dev/tty.usbserial-A602KBWT' #Nano

import serial
import sys

s = serial.serialposix.Serial(port=DEV,
                                      baudrate=9600, bytesize=8, parity='N', stopbits=1,
                                      timeout=120,
                                      rtscts=1)
while True:
    txt = s.readline()
    sys.stdout.write(txt)

